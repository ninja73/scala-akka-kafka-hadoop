package com.ninja73.kafka

import java.util

import com.ninja73.common.domain.Event
import com.ninja73.common.util.JsonSupport
import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer}
import spray.json._

object KafkaJsonSupport {

  class KafkaJsonSerializer[T <: Event : JsonFormat] extends Serializer[T] with JsonSupport {

    val stringSerializer = new StringSerializer

    def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
      stringSerializer.configure(configs, isKey)

    def serialize(topic: String, data: T): Array[Byte] = {
      stringSerializer.serialize(topic, data.toJson.compactPrint)
    }

    def close(): Unit = stringSerializer.close()

  }

  class KafkaJsonDeserializer[T <: Event : JsonFormat] extends Deserializer[T] with JsonSupport {

    val stringDeserializer = new StringDeserializer

    def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
      stringDeserializer.configure(configs, isKey)

    def deserialize(topic: String, data: Array[Byte]): T = {
      val eventJson = stringDeserializer.deserialize(topic, data).parseJson
      eventJson.convertTo[T]
    }

    def close(): Unit = stringDeserializer.close()
  }

}