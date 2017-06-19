package de.htwg.bigdata.flink

case class AntPosition(id: String, x: Int, y: Int, timestamp: Long, moved: Boolean = false ){
   override def toString( ): String = List(id,x,y,timestamp,moved) mkString ","
}
object AntPosition {
  def fromString(line: String): AntPosition = {
    val tokens: Array[String] = line.split(",")
    if (tokens.length != 5) {
      throw new RuntimeException("Invalid record: " + line)
    }
    println(tokens(0))
    new AntPosition(tokens(0),tokens(1).toInt,tokens(2).toInt,tokens(3).toLong,tokens(4).toBoolean) 
  }
  def isMoved(moved: String): Boolean = {
    moved.toBoolean
  }
}
