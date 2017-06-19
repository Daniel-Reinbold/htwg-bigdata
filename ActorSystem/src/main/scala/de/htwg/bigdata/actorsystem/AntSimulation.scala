package de.htwg.bigdata.actorsystem

import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.ExecutionContextExecutor
import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import net.liftweb.json._
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.{Future}
import scala.util.{Failure, Success}
import scala.util.Random

import akka.http.scaladsl.model.HttpEntity.apply
import akka.http.scaladsl.model.Uri.apply
/**
  * Created by tim on 06.04.17.
  */

case class AppConfiguration(antNo: Int, destX: Int, destY: Int)

object AntSimulation extends DefaultJsonProtocol {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: Materializer = ActorMaterializer()
  implicit val formats = DefaultFormats
  implicit val appConfigurationFormat = jsonFormat3(AppConfiguration)
    
  private val counter = new AtomicInteger()
  private val random = scala.util.Random
  implicit def executor: ExecutionContextExecutor = system.dispatcher
  val config = ConfigFactory.load()
  var mainServerAdresse: String = config.getString("server")
  //Default values
  var antNumber:Int = 10
  var targetPosition: Position = Position(10, 10)

  def getCounter = counter.incrementAndGet

  
  def main(args: Array[String]) {
     val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(GET, uri = "http://" + mainServerAdresse + "/config", entity = ""))
      responseFuture onComplete { 
        case Success(posts) => { 
          val appConfigurations = Unmarshal(posts.entity.withContentType(ContentTypes.`application/json`)).to[AppConfiguration] 
          for (appConfiguration<- appConfigurations) {
            this.antNumber = appConfiguration.antNo
            this.targetPosition = Position(appConfiguration.destX, appConfiguration.destY)
          }
          /**
          * tell mainserver that a new simulation has started
          */
          Http().singleRequest(HttpRequest(uri = "http://" + AntSimulation.mainServerAdresse + "/newsimulation", entity = ""))
 
          val antSystem = ActorSystem("antsystem")
          println("AntSimulation-Start - MainServer on " + mainServerAdresse )
          for (it <- 1 to antNumber) {
            val myActor = antSystem.actorOf(Props(new Ant()))
            val waitDuration = random.nextDouble()
            system.scheduler.scheduleOnce(waitDuration seconds,myActor,"start")
          }
        }
        case Failure(e) => println(e);
      }
    
  }
}
