import com.typesafe.sbt.packager.Keys.dockerEntrypoint

lazy val commonSetting = Seq(
  organization := "com.ninja73",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

lazy val dockerSetting = Seq(
  publishArtifact in packageSrc := false,
  dockerBaseImage := "java:8-jre",
  dockerRepository := Some("localhost"),
  dockerUpdateLatest := true,
  dockerExposedPorts := Seq(9400),
  mappings in Universal += {
    file("docker/docker-entry-point.sh") -> "bin/docker-entry-point.sh"
  },
  dockerEntrypoint := Seq("bin/docker-entry-point.sh"))

lazy val common = (project in file("common"))
  .settings(
    commonSetting,
    libraryDependencies ++=
      Dependencies.Logger ++
        Seq(Dependencies.sprayJson))

lazy val kafka = (project in file("kafka"))
  .settings(
    commonSetting,
    libraryDependencies ++= Dependencies.Kafka)
  .dependsOn(common)

lazy val api = (project in file("api"))
  .settings(
    commonSetting,
    libraryDependencies ++= Dependencies.Akka)
  .dependsOn(kafka)

lazy val hiveWriter = (project in file("hive-writer"))
  .settings(commonSetting)
  .dependsOn(kafka)