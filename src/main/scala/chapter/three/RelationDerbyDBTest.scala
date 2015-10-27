package chapter.three

import java.sql.ResultSet

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.rdd.{JdbcRDD, RDD}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.rdd.JdbcRDD._
import java.sql.DriverManager


/**
 * Created by smartins on 10/20/15.
 */
object RelationDerbyDBTest {

  def main(args: Array[String]) {

      // define the configuration
    val conf = new SparkConf()
    conf.setAppName("My relational DB test")
    conf.setMaster("spark://127.0.0.1:7077")
    // define the context()
    val ctx = new SparkContext(conf)
    // define JDBC RDD
    val rdd = new JdbcRDD(ctx, () => DriverManager.getConnection("jdbc:derby:/usr/local/apache/MyDbTest1; create=true"),
      "SELECT ID,Name, age FROM EMP WHERE Age >= ? AND ID <= ?",
      20, 30, 3, (r: ResultSet) => { r.getInt(1); r.getString(2) } ).cache()

    // Print just the first column of the result set
    System.out.println(rdd.first)
  }
}
