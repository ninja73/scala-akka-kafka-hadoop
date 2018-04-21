package com.ninja73.api.route

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.ninja73.common.domain.SomeEvent
import com.ninja73.common.util.JsonSupport
import com.ninja73.kafka.KafkaProducer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

class EventRoute(implicit system: ActorSystem)
  extends Directives with JsonSupport with LazyLogging {

  implicit val context: ExecutionContext = system.dispatcher
  val producer: KafkaProducer[SomeEvent] = KafkaProducer("localhost", "topic")

  val route: Route = pathPrefix("api/event") {
    post {
      entity(as[SomeEvent]) { someEvent ⇒
        producer.sendMessageToKafka(someEvent)
        complete(StatusCodes.OK)
      }
    }
  }
}

object EventRoute {
  def apply()(implicit system: ActorSystem): EventRoute = new EventRoute()
}