package com.ninja73.kafka

import akka.actor.{ActorRef, ActorSystem}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{Keep, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import org.apache.kafka.clients.producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.ExecutionContextExecutor

sealed trait Producer[T] {
  def sendMessage(msg: T): Unit
}

class KafkaProducer(actor: ActorRef, host: String)(implicit system: ActorSystem) extends Producer[String] {

  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer.create(system)

  val settings: ProducerSettings[Array[Byte], String] =
    ProducerSettings(system, new ByteArraySerializer, new StringSerializer)
      .withBootstrapServers(host)

  val kafkaProducer: producer.KafkaProducer[Array[Byte], String] = settings.createKafkaProducer()

  val source: Source[String, ActorRef] = Source.actorRef(1000, OverflowStrategy.backpressure)

  val producerActor: ActorRef = source
    .map { s ⇒ new ProducerRecord[Array[Byte], String]("topic1", s) }
    .toMat(Producer.plainSink(settings, kafkaProducer))(Keep.left)
    .run()

  def sendMessage(msg: String): Unit = {
    producerActor ! msg
  }
}