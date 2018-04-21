package com.ninja73.kafka

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.ninja73.common.domain.Event
import com.ninja73.kafka.KafkaJsonSupport.KafkaJsonSerializer
import org.apache.kafka.clients.producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import spray.json.JsonFormat

import scala.concurrent.ExecutionContextExecutor


class KafkaProducer[T <: Event : JsonFormat](host: String, topic: String)(implicit system: ActorSystem) {

  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)

  val settings: ProducerSettings[Array[Byte], T] =
    ProducerSettings(system, new ByteArraySerializer, new KafkaJsonSerializer[T])
      .withBootstrapServers(host)

  val kafkaProducer: producer.KafkaProducer[Array[Byte], T] = settings.createKafkaProducer()

  val source: Source[T, ActorRef] = Source.actorRef(1000, OverflowStrategy.backpressure)

  val producerActor: ActorRef = source
    .map { s ⇒ new ProducerRecord[Array[Byte], T](topic, s) }
    .toMat(Producer.plainSink(settings, kafkaProducer))(Keep.left)
    .run()

  def sendMessageToKafka(msg: T): Unit = {
    producerActor ! msg
  }
}