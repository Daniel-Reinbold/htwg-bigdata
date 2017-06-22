package de.htwg.bigdata.mainserver

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

class AntPositionSchema extends DeserializationSchema[AntPosition] with SerializationSchema[AntPosition]{
/**
 * Implements a SerializationSchema and DeserializationSchema for AnpPosition for Kafka data sources and sinks.
 */
  override def serialize(antPosition: AntPosition): Array[Byte] = antPosition.toString.getBytes
  override def deserialize(message: Array[Byte]): AntPosition = AntPosition.fromString(new String(message))
  override def isEndOfStream(nextElement: AntPosition): Boolean = false
  override def getProducedType(): TypeInformation[AntPosition] =  TypeExtractor.getForClass(classOf[AntPosition])
}
