package com.ninja73.common.domain

sealed trait Event

case class SomeEvent(text: String) extends Event
