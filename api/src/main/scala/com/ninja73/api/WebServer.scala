package com.ninja73.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.ninja73.api.route.EventRoute

import scala.concurrent.ExecutionContextExecutor

object WebServer extends App {

  implicit val system: ActorSystem = ActorSystem("api-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val router: Route = {
    new EventRoute().route
  }

  val bindingFuture = Http().bindAndHandle(router, "localhost", 9090)

  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ ⇒ system.terminate())
  }
}
