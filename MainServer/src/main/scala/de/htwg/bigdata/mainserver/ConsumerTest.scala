package de.htwg.bigdata.mainserver

import java.util.Properties
import java.util.concurrent.{ Executors, ThreadFactory, TimeoutException }
import org.apache.kafka.clients.consumer._
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.{ Deserializer, StringDeserializer }
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.concurrent._
import scala.util.Random
import scala.util.control.NonFatal
import java.util.{Date, Properties, List}
import org.apache.zookeeper.ZooKeeper
import scala.util.parsing.json.JSON
import scala.collection.mutable.{Buffer, ListBuffer}
import collection.JavaConverters._

object ConsumerTest {
  def main(args: Array[String]): Unit = {
    val zk: ZooKeeper = new ZooKeeper("192.168.99.100:2181", 10000, null);
    val ids: Buffer[String] = zk.getChildren("/brokers/ids", false).asScala
    val brokers = new ListBuffer[String]()
      
    ids.foreach{ id => 
      val jsonMap = JSON.parseFull(new String(zk.getData("/brokers/ids/" + id, false, null))).get.asInstanceOf[Map[String, Any]]
      val port = jsonMap.get("port").get.asInstanceOf[Double].toInt
      val host = jsonMap.get("host").get.asInstanceOf[String]
      brokers += host + ":" + port 
    }    
    println("Kafka Broker: " + brokers.mkString(" , " ))    
    val properties = new Properties()
    var topic:String = "ants"
    var data:Array[Byte] = new Array[Byte](5)
    properties.put("bootstrap.servers", brokers.mkString(","))
    properties.put("group.id", "consumer-tutorial")

    properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    properties.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
    val kafkaConsumer = new KafkaConsumer[String, Array[Byte]](properties)
    
    kafkaConsumer.subscribe(Seq("ants"))

    while (true) {
      val results = kafkaConsumer.poll(100)
      for (consumerRecord <- results) {
        println(consumerRecord )
      }
    }
  }
}