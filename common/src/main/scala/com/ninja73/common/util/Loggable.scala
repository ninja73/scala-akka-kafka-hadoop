package com.ninja73.common.util

import org.slf4j.{Logger, LoggerFactory}

trait Loggable {

  val logger: Logger = LoggerFactory.getLogger(getClass.getSimpleName)

}
