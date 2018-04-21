package com.ninja73.api.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.LazyLogging

object CustomExceptionHandler extends LazyLogging {
  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case _ ⇒ extractUri { uri ⇒
      logger.error(s"Something went wrong, route $uri")
      complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Something went wrong, route $uri"))
    }
  }
}
