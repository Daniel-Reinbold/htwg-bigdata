package de.htwg.bigdata.mainserver

import org.mongodb.scala.bson.{BsonInt32, BsonString}
import org.mongodb.scala.{Completed, FindObservable, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import com.typesafe.config
import com.typesafe.config.ConfigFactory

object Database {
  var collectionName = ""
  println("initialize mongoclient")
  val mongoClient = MongoClient("mongodb://192.168.99.100:27017")
  val db = mongoClient.getDatabase("ants")
  def updateAnt(antPosition: AntPosition) {
    val doc: Document = Document("timestamp" -> antPosition.timestamp, "id" -> antPosition.id, "x" -> antPosition.x, "y" -> antPosition.y, "moved" -> antPosition.moved)
    db.getCollection(collectionName).insertOne(doc).subscribe(
      (next: Completed) => (),
      (e: Throwable) => println(e.getMessage),
      () => ()
     )
  }
  def generateNewCollectionName(): Unit ={
    var highestNumber = DatabaseRead.getHighestCollectionNamesNumber()+1
    collectionName = "collection"+highestNumber
    val config=ConfigFactory.load()
    val doc: Document = Document("rows" -> config.getString("fieldWith.rows").toInt, "columns" -> config.getString("fieldWith.columns").toInt, "destX" -> config.getString("destination.x").toInt, "destY" -> config.getString("destination.y").toInt)  
    val database: MongoDatabase = mongoClient.getDatabase("ants")
    val collection: MongoCollection[Document] = database.getCollection(collectionName)
    collection.insertOne(doc).subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("New Collection Inserted")
      override def onError(e: Throwable): Unit = println("Collection Insertion Failed")
      override def onComplete(): Unit = println("Collection Insertion Completed")
    })
  }
  def readAnts(): Unit = {
/*
    val database: MongoDatabase = mongoClient.getDatabase("ants")
    val collection: MongoCollection[Document] = database.getCollection("ants")
    val observable: Observable[Document] = collection.find(exists("timestamp")).sort(ascending("timestamp"))

    observable.subscribe(new Observer[Document] {
      override def onNext(result: Document): Unit = {
        //println("onNext")
        //println(result.toJson())
      }
      override def onError(e: Throwable): Unit = println("Failed" + e.getMessage)
      override def onComplete(): Unit = {
        println("Completed")
      }
    })

    for (document: Document <- observable) {
      println(document.toJson())
      //var timestamp = document.get[BsonString]("id")
      var id = document.get[BsonString]("id") map (_.asString().getValue)
      var x = document.get[BsonInt32]("x") map (_.asInt32().getValue)
      var y = document.get[BsonInt32]("y") map (_.asInt32().getValue)
    }
    println("Completed")
    //return observable*/
  }
}
