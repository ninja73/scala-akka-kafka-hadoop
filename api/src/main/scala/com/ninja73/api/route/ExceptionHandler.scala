package com.ninja73.api.route

import akka.http.scaladsl.server._
import Directives._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import com.ninja73.common.util.Loggable

object ExceptionHandler extends Loggable {
  implicit def exceptionHandler: ExceptionHandler = ExceptionHandler {
    case _ ⇒ extractUri {uri ⇒
      logger.error(s"Something went wrong, route $uri")
      complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Something went wrong, route $uri"))
    }
  }
}
