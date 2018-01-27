package com.ninja73.api.route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.ninja73.common.domain.SomeEvent
import com.ninja73.common.util.{JsonSupport, Loggable}

import scala.concurrent.ExecutionContext

class EventRoute(implicit val context: ExecutionContext)
  extends Directives with JsonSupport with Loggable {

  val route: Route = pathPrefix("api/event") {
    post {
      entity(as[SomeEvent]) { _ ⇒
        complete(StatusCodes.OK)
      }
    }
  }
}
