package de.htwg.bigdata.mainserver

import com.mongodb.casbah.Imports._
import scala.collection.mutable

object DatabaseRead {

  var currentMillis:Long = 0;
  val mongoClient = MongoClient("192.168.99.100", 27017)
  val db = mongoClient("ants")

  def convertDbObjectToAnt(obj: MongoDBObject): AntPosition = {
    val id = obj.getAs[String]("id").get
    val x = obj.getAs[Int]("x").get
    val y = obj.getAs[Int]("y").get
    val timestamp = obj.getAs[Long]("timestamp").get
    AntPosition(id, x, y, timestamp)
  }

  def getFirstTimestamp(collectionname:String):Long={

    val collection = db(collectionname)

    var ants = collection.find().limit(1)
    var timestamp:Long = 0
    for(antDBObject<-ants){
      var ant = convertDbObjectToAnt(antDBObject)
      timestamp = ant.timestamp
    }
    return timestamp
  }

  def getHighestCollectionNamesNumber(): Int ={

    val collectionNames:mutable.Set[String] = db.collectionNames

    var number = 0;
    for(collectionName <- collectionNames){
      var numberAsString = collectionName.split("n")
      val localNumber = numberAsString(1).toInt
      if(localNumber>number){
        number = localNumber
      }
    }
    return number
  }

  def readAnts(collectionname:String):  List[AntPosition]={

    if(currentMillis ==0){
      currentMillis = getFirstTimestamp(collectionname)
    }

    val collection = db(collectionname)

    var ants = collection.find("timestamp" $gte currentMillis $lt currentMillis+1000 )

    var antsSeq: List[AntPosition] = List()

    for(antDBObject<-ants){
      var ant = convertDbObjectToAnt(antDBObject)
      antsSeq :+= ant
    }

    currentMillis+=1000

    return antsSeq
  }
}
