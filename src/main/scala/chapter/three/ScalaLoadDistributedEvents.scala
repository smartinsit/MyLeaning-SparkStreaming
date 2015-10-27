package chapter.three

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.flume._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd._
import org.apache.spark.streaming.dstream._
import java.net.InetSocketAddress
import java.io.ObjectOutputStream
import java.io.ObjectOutput
import java.io.ByteArrayOutputStream

/**
 * Created by smartins on 10/24/15.
 */
object ScalaLoadDistributedEvents {

  def main (args: Array[String]){
    println("Creating Spark Configuration")
    // Create an object of spark configuration
    val conf = new SparkConf()
    // set the logical and user defined name of this application
    conf.setAppName("Flume Streaming Data Load Application")

    println("Retrieving Streaming Context from Spark Conf")

    /*
    Retrieving Streaming Context from Spark conf object
    Second parameter is the time interval at which the
    streaming data will be divided into batches.
     */
    val strCtx = new StreamingContext(conf,Seconds(2))

    /*
    Create an array of InetSocketAddress contaning the host and
    the port of the machines
    Where Flume sink is delivering the events
    Basically it is the value of the following properties
    defined in Flume config:
    (1) a1.sinks.spark1.hostname
    (2) a1.sinks.spark1.port
    (1) a2.sinks.spark2.hostname
    (2) a2.sinks.spark2.port
     */
    println("Defining the array of InetSocketAddress from Flume ")
    var addresses = new Array[InetSocketAddress](2)

    addresses(0) = new InetSocketAddress("127.0.0.1",4949)
    addresses(1) = new InetSocketAddress("127.0.0.1",4950)

    /*
    Create a Flume polling stream which will poll the sink
    to get the events from the sink every 2 seconds
    Last 2 parameters of this method are important as the
    1. maxBatchSize = it is the maximum number of events to be pulled
    from the Spark sink in a single RPC call
    2. parallelism - The number of concurrent request this
    stream should send to the sink. For more info go to
    https://spark.apache.org/docs/1.1.0/api/java/org/apache/spark/streaming/flume/FlumeUtils.html
     */

    println("Create a Flume polling stream. It is a Spark utility")
    val flumeStream = FlumeUtils.createPollingStream(strCtx,addresses,StorageLevel
      .MEMORY_AND_DISK_SER_2,1000,1)

    // Define output stream connect to console for printing the results

    println("Define the output stream. Going to console")
    val outputStream = new ObjectOutputStream(Console.out)

    // Invoking custom Print Method for writing events to console

    println("Calling printValus function")
    printValues(flumeStream, strCtx, outputStream)

    // Most important statement which will initiate the stream context

    println("calling the streaming context start() function")
    strCtx.start()

    // Wait till the execution is completed
    println("calling the streaming context awaitTermination() function")
    strCtx.awaitTermination()

  }
  def printValues(stream:DStream[SparkFlumeEvent],strCxt:StreamingContext, outputStream: ObjectOutput): Unit = {
    println("Calling stream.foreachRDD...")
    stream.foreachRDD(foreachFunc)
    /*
    SparkFlumeEvent is the wrapper classes containing all
    the events captured by the Stream
      */
    def foreachFunc = (rdd: RDD[SparkFlumeEvent]) => {
      val array = rdd.collect()
      println("------ Start printing results ------")
      println("Total size of events = " + array.size)
      for (flumeEvent<-array) {
        /*
         This is to get the AvroFlumeEvent from SparkFlumeEvent
         for printing the original data
          */
        val payLoad = flumeEvent.event.getBody()
        // Printing the actual events captured by the stream
        println(new String(payLoad.array()))
      }
      println("------ Finished Printing Results -----")
    }

  }
}
