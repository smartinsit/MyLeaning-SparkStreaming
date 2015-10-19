package chapter.two

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming._
import org.apache.spark.storage.StorageLevel._
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.dstream.ForEachDStream


/**
 * Created by smartins on 10/15/15.
 * This is my first spark streaming code copied from the book
 * Testing...
 */
object ScalaFirstStreamingExample {
  def main(args: Array[String]) {

    println("Creating Spark configuration")
    // Create an spark object of Spark configuration
    val conf = new SparkConf()

    // Set the logical and the user define name for this application
    conf.setAppName("My First Spark Streaming Application")

    println("Retrieving Spark Streaming Context from SparkConf")

    /*
    Retrieving Streaming Context from Spark Conf
    Second parameter is the time interval at which
    streaming data will devied into batches
    */
    val streamCtx = new StreamingContext(conf, Seconds(2))

    /*
    Define the type of the stream. Here we are using TCP Socket
    as text stream, it will keep watching for the incoming data
    from a specific machine (localhost) and port (9087)
    Once the data is retrieved it will be saved in memory and
    in case the memory is not sufficient, then it will store it
    on the Disk. It will further read the data and convert it into
    DStream
     */
    val lines = streamCtx.socketTextStream("127.0.0.1", 9087, MEMORY_AND_DISK_SER_2)

    /*
    Apply the split() function to all element in the DStream
    which will generate multiple new record from each record
    on the source stream.
    And then use flatmap() to consolidate all the records and
    create a new DStream
     */
    val words = lines.flatMap(x => x.split(" "))

    /*
    Now we will count these words by applying a using map()
    map() helps in applying a function to each element in the
    RDD.
     */
    val pairs = words.map(word => (word,1))

    /*
    Further we will aggregate the value of each key by
    using/applying the giving function
     */
    val wordCounts = pairs.reduceByKey(_ + _)

    // Lastly we will print all values
    // wordCounts.print(20)
    printValues(wordCounts,streamCtx)

    // Most important statement which will initiate the streamingCtx
    streamCtx.start()

    // Wait till the execution si completed
    streamCtx.awaitTermination()
  }

  def printValues(stream:DStream[(String, Int)], streamCtx:StreamingContext): Unit = {
    stream.foreachRDD(foreachFunc)
    def foreachFunc = (rdd: RDD[(String, Int)]) => {
      val array = rdd.collect()
      println("------------- Start Printing Results -------------")
      for (res<-array) {
        println(res)
      }
      println("------------- Finish Printing Results -------------")

    }
  }

}
