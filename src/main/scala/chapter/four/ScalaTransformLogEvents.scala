package chapter.four


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
 * Created by smartins on 10/28/15.
 */
object ScalaTransformLogEvents {

  def main(args:Array[String]): Unit = {
    /*
    Start common piece of code for all kinds of
    transform operations
     */
    println("Creating Spark configuration")
    val conf = new SparkConf()
    conf.setAppName("Apache Log Transformer")
    println("Retrieving Streaming Context from Spark Conf")

    val streamCtx = new StreamingContext(conf, Seconds(10))

    val addresses = new Array[InetSocketAddress](1)
    addresses(0) = new InetSocketAddress("127.0.0.1", 4949)

    val flumeStream = FlumeUtils.createPollingStream(streamCtx, addresses, StorageLevel.MEMORY_AND_DISK_SER_2, 1000, 1)


    // Utility class to transform log data
    val transformLog = new ScalaLogAnalyzer()

    /*
    Invoking flatMap operation to flattening the results and convert
    them into key/value pairs
     */
    val newDStream = flumeStream.flatMap { x => transformLog.transformLogData(new String(x.event.getBody().array())) }

    /* Count request type */
    val transformedRDD = newDStream.transform(functionCountRequestType)

    /* Enable the checkpoint and print values */
    streamCtx.checkpoint("checkpointDir")
    transformedRDD.updateStateByKey(functionTotalCount).print(100)
    /*
    End common piece of code for all kind of transform operations
     */

    /* Start - transform functions */
    println("Calling executeTransformations(newDStream, streamCtx) ")
    executeTransformations(newDStream, streamCtx)
    /* End - Transform functions */

    println("Calling streamCtx.start() ")
    streamCtx.start()
    println("Calling streamCtx.awaitTermination() ")
    streamCtx.awaitTermination()

  }

  /*
  Define and execute all transformations to the log data
   */
  def executeTransformations(dStream:DStream[(String, String)], streamCtx: StreamingContext): Unit = {
    /* Start - printing all attributes from apache access log */
    println("Starting function executeTransformations")
    println("Call printLogValues - print all attributes")
    printLogValues(dStream, streamCtx)
    /* End - Print all attributes of Apache Access Log
     */

    /* Counting the number of GET requests in the stream */
    println("Counting the number of GET requests")
    dStream.filter(x=> x._1.equals("method") && x._2.contains("GET")).count().print()

    /* Count the distinctive request for requested URL
    The map function will build a K/V pair where the key will be the URL
    and the value will be the count, in this case 1
    */
    println("Counting the distinctive requests")
    val newStream = dStream.filter(x=> x._1.contains("request")).map(x=> (x._2,1))
    newStream.reduceByKey(_+_).print(100)
    println("Last line in the function executeTransformations ")

    /* Start - Windowing Operation */
    executeWindowsOperations(dStream, streamCtx)
    /* End - Windowing Operation */

  }

  def printLogValues(stream:DStream[(String, String)], streamCtx: StreamingContext) {
    /* Implementing forEach function for printing all the data
    in the provided DStream
     */
    println("Started function printLogValues")
    stream.foreachRDD(foreachFunc)
    /* Define the foreachFunc and print the values in the console */
    def foreachFunc = (rdd: RDD[(String, String)]) => {
      /* Collect() method fetches data from all partition and "collects"
      at the driver node. So in case the data is too huge the driver may crash
      In production we persist this RDD in HDFS or use the rdd.take(n) method
      */
      println("Inside foreachFunc() - Before creationg the array from RDD")
      val array = rdd.collect()

      println("------ Start printing Results ---------")
      for (dataMap<-array.array) {
        println(dataMap._1, " ------ ", dataMap._2)
      }
      println("--------- Finish Printing Results ---------")

    }
  }

  def functionCountRequestType = (rdd:RDD[(String, String)]) => {
    rdd.filter(f=>f._1.contains("method")).map(x=>(x._2,1)).reduceByKey(_+_)
  }

  def functionTotalCount = (values: Seq[Int], state: Option[Int]) => {
    Option(values.sum + state.sum)
  }

  def executeWindowsOperations(dStream:DStream[(String, String)], streamCtx: StreamingContext) {
    /* this provides the aggregate count for all response codes */
    println("Printing count of response code using windown operations")
    val wStream = dStream.window(Seconds(40), Seconds(20))
    val respCodeStream = wStream.filter(x=>x._1.contains("respCode")).map(x=>(x._2,1))
    respCodeStream.reduceByKey(_+_).print(100)

    /* This provides an aggregate count of all response codes
    by using Window operation in reduce method
     */
    println("Printing count of respopnse code using reduceByKeyAndWindow operation")
    val respCodeStream_1 = dStream.filter(x=>x._1.contains("respCode")).map(x=>(x._2,1))
    respCodeStream_1.reduceByKeyAndWindow((x: Int, y: Int) => x+y, Seconds(40), Seconds(20)).print(100)

    /* This will apply and print groupByKeyAndWindow in the slide window */
    println("Applying and printing groupbyKeyAndWindow in a slide window")
    val respCodeStream_2 = dStream.filter(x=>x._1.contains("respCode")).map(x=>(x._2,1))
    respCodeStream_2.groupByKeyAndWindow(Seconds(40),Seconds(20)).print(100)

  }
}
