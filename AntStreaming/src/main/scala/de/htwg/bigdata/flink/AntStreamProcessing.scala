package de.htwg.bigdata.flink

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.assigners._;
import org.apache.flink.streaming.util.serialization.{DeserializationSchema, SerializationSchema}
import org.apache.flink.streaming.api.datastream.DataStreamSource
import org.apache.flink.api.common.typeinfo.TypeInformation
import scala.collection.JavaConverters._
import scala.util.Try
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.zookeeper.ZooKeeper
import scala.util.parsing.json.JSON
import scala.collection.mutable.{Buffer, ListBuffer}
import collection.JavaConverters._
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction,RequestIndexer,ActionRequestFailureHandler}
import org.apache.flink.api.common.functions.{RuntimeContext, MapFunction}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.action.ActionRequest
import org.elasticsearch.client.Requests
import org.elasticsearch.common.network.NetworkService
import org.elasticsearch.common.util.concurrent.EsRejectedExecutionException
import org.apache.flink.util.ExceptionUtils
import org.elasticsearch.ElasticsearchParseException
import java.util.Properties
import java.net.{InetSocketAddress,InetAddress}
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import java.util
import com.google.gson.Gson
import scala.collection.JavaConversions._

object AntStreamProcessing {

  def main(args: Array[String]) {
    if (args.length != 5) {
      System.err.println("Usage: <zookeeper IP> <zookeeper port> <elasticsearch IP> <elasticsearch port> <elasticsearch cluster>")
      return
    }
    
    val senv = StreamExecutionEnvironment.getExecutionEnvironment
    //get broker from zookeeper
    val zk: ZooKeeper = new ZooKeeper(args(0)+":"+args(1).toInt, 10000, null);
    val ids: Buffer[String] = zk.getChildren("/brokers/ids", false).asScala
    val brokers = new ListBuffer[String]()    
    ids.foreach{ id => 
      val jsonMap = JSON.parseFull(new String(zk.getData("/brokers/ids/" + id, false, null))).get.asInstanceOf[Map[String, Any]]
      val port = jsonMap.get("port").get.asInstanceOf[Double].toInt
      val host = jsonMap.get("host").get.asInstanceOf[String]
      brokers += host + ":" + port 
    }     
    val properties = new Properties()
    properties.put("zookeeper.connect",args(0)+":"+args(1).toInt)
    properties.put("bootstrap.servers", brokers.mkString(","))
    properties.put("group.id","flinkProcessingUnion")
    properties.put("auto.offset.reset","earliest")
    
    //create an AntPosition data stream
    val antMoves: DataStream[AntPosition] = senv.addSource(new FlinkKafkaConsumer010[AntPosition]("ants", new AntPositionSchema, properties) ).name("Kafka 0.10.2.1")
    
    //create sink
    val userConfig = new java.util.HashMap[String, String]
    userConfig.put("cluster.name", args(4).toString)
    // This instructs the sink to emit after every element, otherwise they would be buffered
    userConfig.put("bulk.flush.max.actions", "10")			
	  
    val transportAddresses = new java.util.ArrayList[InetSocketAddress]
    transportAddresses.add(new InetSocketAddress(InetAddress.getByName(args(2)), args(3).toInt))

    var time = TumblingProcessingTimeWindows.of(Time.milliseconds(100));
    
    val esSink = new ElasticsearchSink(
        userConfig,
        transportAddresses, 
        new ElasticsearchSinkFunction[AntPosition] {
          override def process(record: AntPosition, ctx: RuntimeContext, indexer: RequestIndexer) {
            val indexRequest = new IndexRequest("ants-idx", "movement")
            indexRequest.source(new Gson().toJson(record), XContentType.JSON)
            println(new Gson().toJson(record))
            indexer.addIndex(indexRequest)
          }
        },
        new ActionRequestFailureHandler {
                override def onFailure(
                        action: ActionRequest,
                        failure: Throwable,
                        restStatusCode: Int,
                        indexer: RequestIndexer ) {      
                    if (ExceptionUtils.containsThrowable(failure, classOf[EsRejectedExecutionException])) {
                        // full queue; re-add document for indexing
                        indexer.add(action)
                    } else if (ExceptionUtils.containsThrowable(failure, classOf[ElasticsearchParseException])) {
                        // malformed document; simply drop request without failing sink
                    } else {
                        // for all other failures, fail the sink
                        throw failure
                    }
                }
     })
    
    val ants = antMoves
        .keyBy("id")
        .window(time)
        .maxBy("timestamp")
//        .filter(k => k.moved)
//        .map(x=>(x.id,x.x,x.y))
//        .windowAll(time)
//        .fold(List.empty[(String, Int, Int)]) { (acc, v) =>  v :: acc}
//        .addSink(antGridVisualization.getSink())
        //Add ElasticSearch Sink
        .addSink(esSink).name("ElasticSearch 5.4.1")
    //execute transformation pipeline  
    senv.execute()

  }
}
