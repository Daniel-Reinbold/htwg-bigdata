name := "ActorSystem"
version := "1.0"
scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV       = "2.5.1"
  val akkaHttpV	  = "10.0.6"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "net.liftweb" %% "lift-json" % "2.6"
  )
}

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    mainClass in Compile := Some("de.htwg.bigdata.actorsystem.AntSimulation"),
    dockerBaseImage := "frolvlad/alpine-oraclejdk8",
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(9000)
  )