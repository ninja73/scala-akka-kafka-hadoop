package com.ninja73.common.util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.ninja73.common.domain.SomeEvent
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val someEventFormat: RootJsonFormat[SomeEvent] = jsonFormat2(SomeEvent)
}
