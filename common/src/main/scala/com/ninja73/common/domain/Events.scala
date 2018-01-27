package com.ninja73.common.domain

sealed trait Event {
  val eventType: String
}

case class SomeEvent(eventType: String, text: String) extends Event
