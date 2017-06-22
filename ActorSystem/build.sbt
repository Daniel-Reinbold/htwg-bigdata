import com.typesafe.sbt.packager.docker.{Cmd}

name := "actorsystem"
version := "1.0"
scalaVersion := "2.11.8"

maintainer in Docker := "HTWG Konstanz"

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
	maintainer in Docker := "HTWG Konstanz",
    dockerBaseImage := "frolvlad/alpine-oraclejdk8",
	dockerCommands := dockerCommands.value.flatMap{
	  case cmd@Cmd("FROM",_) => List(cmd,Cmd("RUN", "apk update && apk add bash"), Cmd("EXPOSE", "9000"))
	  case other => List(other)
	}

  )