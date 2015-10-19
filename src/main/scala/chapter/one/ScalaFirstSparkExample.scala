package chapter.one

import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by smartins on 10/11/15.
 */
object ScalaFirstSparkExample {
  def main(args: Array[String]) {
    println("Creating Spark Configuration")
    // Create an Object of Spark configuration
    val conf = new SparkConf()
    // set the logical and the user define name for this application
    conf.setAppName("My first Spark Scala application")
    /*
    Define the URL of the Spark master
    Useful only if you are executing scala app directly from the Console
    We will comment it by now but will use later
    */
    conf.setMaster("spark://127.0.0.1:7077")

    println("Creating Spark Context")
    // Create the Spark Context and provide the previously created
    // object of SparkContext as a reference
    val ctx = new SparkContext(conf)
    println("Loading the Dataset and will further process it")
    /*
    defining and loading the text file from local
    file system or HDFS and convert into RDD.
    SparkContext.textFile(..) - It uses the Hadoop's TextInputFormat
    and file is broken by the new line character
    refer to Refer to http://hadoop.apache.org/docs/r2.6.0/api/org/apache/hadoop/mapred/TextInputFormat.html
    The Second Argument is the Partitions which specify the parallelism.
    It should be equal or more then number of Cores in the cluster.
    */
    val file = System.getenv("SPARK_HOME")+"/README.md";
    val logData = ctx.textFile(file,2)
    /*
    Invoking Filter operation on the RDD and counting the number of lines
    in the Data loaded in RDD. Simply returning true as "TextInputFormat"
    have already divided the data by "\n" So each RDD will have only 1 line.
    */
    val numLines = logData.filter(line => true).count()
    //Finally Printing the Number of lines.
    println("Number of lines in the dataset " + numLines)


  }

}
