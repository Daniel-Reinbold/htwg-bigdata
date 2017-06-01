import com.typesafe.sbt.packager.docker.{Cmd}

name := "MainServer"
version := "1.0"
scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV       = "2.5.1"
  val akkaHttpV	  = "10.0.6"
  val flinkV	  = "1.1.3"

  Seq(
	"org.apache.zookeeper" % "zookeeper" % "3.3.4" excludeAll(
	    ExclusionRule(name = "jms"),
	    ExclusionRule(name = "jmxtools"),
	    ExclusionRule(name = "jmxri")
	),
    "org.mongodb.scala" %% "mongo-scala-driver" % "2.0.0",
    "org.apache.kafka" % "kafka-clients" % "0.9.0.1",
    "org.apache.flink" % "flink-streaming-scala_2.11" % flinkV,
    "org.apache.flink" % "flink-core" % flinkV,
    "org.mongodb" %% "casbah" % "3.1.1",
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
  .enablePlugins(DockerPlugin)
  .enablePlugins(JavaAppPackaging)
  .settings(
    mainClass in Compile := Some("de.htwg.bigdata.mainserver.MainServer"),
    dockerUpdateLatest := true,    
    dockerBaseImage := "frolvlad/alpine-oraclejdk8",
	dockerCommands := dockerCommands.value.flatMap{
	  case cmd@Cmd("FROM",_) => List(cmd,Cmd("RUN", "apk update && apk add bash"), Cmd("EXPOSE", "27020"))
	  case other => List(other)
	}

  )
