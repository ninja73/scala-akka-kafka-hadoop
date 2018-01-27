package com.ninja73.kafka

import java.util

import com.ninja73.common.domain.Event
import com.ninja73.common.util.JsonSupport
import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer}
import spray.json._

class KafkaJsonSerializer extends Serializer[Event] with JsonSupport {

  val stringSerializer = new StringSerializer

  def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
    stringSerializer.configure(configs, isKey)

  def serialize(topic: String, data: Event): Array[Byte] = {
    stringSerializer.serialize(topic, data.toJson.compactPrint)
  }

  def close(): Unit = stringSerializer.close()

}

class KafkaJsonDeserializer extends Deserializer[Event] with JsonSupport {

  val stringDeserializer = new StringDeserializer

  def configure(configs: util.Map[String, _], isKey: Boolean): Unit =
    stringDeserializer.configure(configs, isKey)

  //TODO заглушка
  def deserialize(topic: String, data: Array[Byte]): Event = {
    val eventJson = stringDeserializer.deserialize(topic, data).parseJson
    eventJson.convertTo[Event]
  }

  def close(): Unit = stringDeserializer.close()
}