import sbt._

object Dependencies {

  val akkaVersion = "2.5.9"
  val akkHttpVersion = "10.1.0-RC1"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  val akkaKafka = "com.typesafe.akka" %% "akka-stream-kafka" % "0.18"
  val akkaHttp =  "com.typesafe.akka" %% "akka-http" % akkHttpVersion
  val sprayJson = "com.typesafe.akka" %% "akka-http-spray-json" % akkHttpVersion

  val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"

  val Akka = Seq(akkaActor, akkaHttp, akkaStream)

  val Kafka = Seq(akkaActor, akkaStream, akkaKafka)

  val Logger = Seq(logback, scalaLogging)
}
