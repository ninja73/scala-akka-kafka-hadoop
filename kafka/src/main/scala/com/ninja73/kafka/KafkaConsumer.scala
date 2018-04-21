package com.ninja73.kafka

import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.ConsumerMessage.CommittableOffsetBatch
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.ninja73.common.domain.Event
import com.ninja73.kafka.KafkaJsonSupport.KafkaJsonDeserializer
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import spray.json.JsonFormat

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class KafkaConsumer[T <: Event : JsonFormat](actor: ActorRef,
                                             bootstrapServers: String,
                                             groupId: String,
                                             topic: String)
                                            (implicit system: ActorSystem)
  extends StrictLogging {

  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)

  val consumerSettings: ConsumerSettings[Array[Byte], T] =
    ConsumerSettings(system, new ByteArrayDeserializer, new KafkaJsonDeserializer[T])
      .withBootstrapServers(bootstrapServers)
      .withGroupId(groupId)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  Consumer.committableSource(consumerSettings, Subscriptions.topics(topic))
    .mapAsync(10) { msg ⇒
      Future(actor ! msg.record.value()).map(_ ⇒ msg.committableOffset)
    }
    .batch(max = 1000l, first ⇒ CommittableOffsetBatch.empty.updated(first)) { (batch, elem) =>
      batch.updated(elem)
    }
    .mapAsync(3)(_.commitScaladsl())
    .runWith(Sink.ignore)
    .onComplete {
      case Success(_) ⇒
        logger.info("Kafka consumer finish.")
      case Failure(e) ⇒
        logger.error(e.getMessage, e)
        system.terminate()
    }

}
