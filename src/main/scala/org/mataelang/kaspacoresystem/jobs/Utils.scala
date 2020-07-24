package org.mataelang.kaspacoresystem.jobs

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import java.sql.{Connection, DriverManager}

import org.mataelang.kaspacoresystem.util.PropertiesLoader

private[jobs] trait Utils {
  def getSparkContext(session: SparkSession): SparkContext = {
    session.sparkContext
  }

  def getSparkSession(strings: Array[String]): SparkSession = {
    val conf = new SparkConf(true)
      .setMaster(PropertiesLoader.sparkMaster)
      .setAppName(PropertiesLoader.sparkAppName)
      .set("spark.app.id", PropertiesLoader.sparkAppId)
      .set("spark.cassandra.output.batch.grouping.key", "Partition")
      .set("spark.cassandra.output.concurrent.writes", "2000")
      .set("spark.submit.deployMode", "client")
      .set("spark.executor.cores", "2")
      .set("spark.executor.memory", "4g")
      .set("spark.scheduler.mode", "FAIR")
      .set("spark.history.fs.cleaner.enabled", "true")
      .set("spark.history.fs.cleaner.maxAge", "12h")
      .set("spark.history.fs.cleaner.interval", "1h")

    val session = SparkSession.builder()
      .config(conf)
      .getOrCreate()

    session
  }

  def getPostgreSQLSession: Connection = {
    classOf[org.postgresql.Driver]

    val connectionUrl = "jdbc:postgresql://" +
      PropertiesLoader.postgresqlHost + ":" +
      PropertiesLoader.postgresqlPort.toString + "/" +
      PropertiesLoader.postgresqlDatabase + "?user=" +
      PropertiesLoader.postgresqlUsername + "&password=" +
      PropertiesLoader.postgresqlPassword

    DriverManager.getConnection(connectionUrl)
  }
}
