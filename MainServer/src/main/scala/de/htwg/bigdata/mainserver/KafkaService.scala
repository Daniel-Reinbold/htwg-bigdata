package de.htwg.bigdata.mainserver

import java.util.{Date, Properties, List}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, Callback, RecordMetadata}
import org.apache.zookeeper.ZooKeeper
import scala.util.parsing.json.JSON
import scala.collection.mutable.{Buffer, ListBuffer}
import collection.JavaConverters._

class KafkaService(topic: String, zooKeeper: String){
  val properties = new Properties()
  //Receive brokers from zookeeper
  val zk: ZooKeeper = new ZooKeeper(zooKeeper, 10000, null);
  val ids: Buffer[String] = zk.getChildren("/brokers/ids", false).asScala
  val brokers = new ListBuffer[String]()    
  ids.foreach{ id => 
    val jsonMap = JSON.parseFull(new String(zk.getData("/brokers/ids/" + id, false, null))).get.asInstanceOf[Map[String, Any]]
    val port = jsonMap.get("port").get.asInstanceOf[Double].toInt
    val host = jsonMap.get("host").get.asInstanceOf[String]
    brokers += host + ":" + port 
  }    
  println("Kafka Broker: " + brokers.mkString(" , " ))
  properties.put("bootstrap.servers", brokers.mkString(","))
  properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  val producer = new KafkaProducer[String, Array[Byte]](properties)
 
  def createProduceRecord(antPosition: AntPosition) {
    producer.send(new ProducerRecord(topic, antPosition.id,new AntPositionSchema( ).serialize(antPosition)))
  }
}

