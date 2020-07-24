package org.mataelang.kaspacoresystem.jobs

import io.confluent.kafka.schemaregistry.client.{CachedSchemaRegistryClient, SchemaRegistryClient}
import org.apache.avro.Schema
import org.apache.spark.sql.ForeachWriter
import org.joda.time.DateTime
import org.apache.spark.sql.avro.SchemaConverters
import org.mataelang.kaspacoresystem.models.{EventObj, Region}
import org.mataelang.kaspacoresystem.util.{AvroDeserializer, ColsArtifact, PropertiesLoader, SQLHelper, Tools}

object DataStream extends Utils {
  private var schemaRegistryClient: SchemaRegistryClient = _
  private var kafkaAvroDeserializer: AvroDeserializer = _

  def lookupTopicSchema(topic: String, isKey: Boolean = false): String = {
    schemaRegistryClient.getLatestSchemaMetadata(topic + (if (isKey) "-key" else "-value")).getSchema
  }

  def avroSchemaToSparkSchema(avroSchema: String): SchemaConverters.SchemaType = {
    SchemaConverters.toSqlType(new Schema.Parser().parse(avroSchema))
  }

  def main(args: Array[String]): Unit = {
    val sparkSession = getSparkSession(args)
    val sparkContext = getSparkContext(sparkSession)

    schemaRegistryClient = new CachedSchemaRegistryClient(PropertiesLoader.schemaRegistryUrl, 128)
    kafkaAvroDeserializer = new AvroDeserializer(schemaRegistryClient)
    sparkSession.udf.register("deserialize", (bytes: Array[Byte]) =>
      kafkaAvroDeserializer.deserialize(bytes)
    )
    
    sparkContext.addFile(PropertiesLoader.GeoIpPath)

    import sparkSession.implicits._
    sparkContext.setLogLevel("ERROR")

    val kafkaStreamDF = sparkSession.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", PropertiesLoader.kafkaBrokerUrl)
      .option("subscribe", PropertiesLoader.kafkaInputTopic)
      .option("startingOffsets", PropertiesLoader.kafkaStartingOffset)
      .load()


    import org.apache.spark.sql.functions._
    val jsonDF = kafkaStreamDF.select(
      callUDF("deserialize", 'key).as("key"),
      callUDF("deserialize", 'value).as("value")
    )

    val dfValueSchema = {
      val rawSchema = lookupTopicSchema(PropertiesLoader.kafkaInputTopic)
      avroSchemaToSparkSchema(rawSchema)
    }

    val parsedRawDf = jsonDF.select(
      'key,
      from_json('value, dfValueSchema.dataType).alias("value")
    ).select(
      $"value.*"
    )

    val eventDf = parsedRawDf.select(
      $"timestamp", $"device_id", $"protocol", $"ip_type", $"src_mac", $"dest_mac", $"src_ip",
      $"dest_ip", $"src_port", $"dst_port", $"alert_msg", $"classification", $"priority", $"sig_id",
      $"sig_gen", $"sig_rev", $"company"
    ).map { r =>
      val ts = r.getAs[String](0)
      val device_id = r.getAs[String](1)
      val protocol = r.getAs[String](2)
      val ip_type = r.getAs[String](3)
      val src_mac = r.getAs[String](4)
      val dest_mac = r.getAs[String](5)
      val src_ip = r.getAs[String](6)
      val dest_ip = r.getAs[String](7)
      val src_port = r.getAs[Long](8).toInt
      val dest_port = r.getAs[Long](9).toInt
      val alert_msg = r.getAs[String](10)
      val classification = r.getAs[Long](11).toInt
      val priority = r.getAs[Long](12).toInt
      val sig_id = r.getAs[Long](13).toInt
      val sig_gen = r.getAs[Long](14).toInt
      val sig_rev = r.getAs[Long](15).toInt
      val company = r.getAs[String](16)

      val regionDataSrc: Region = Tools.IpLookupCountry(src_ip)
      val regionDataDest: Region = Tools.IpLookupCountry(dest_ip)

      val src_country = regionDataSrc.countryName
      val src_region = regionDataSrc.regionName
      val src_city = regionDataSrc.cityName
      val dest_country = regionDataDest.countryName
      val dest_region = regionDataDest.regionName
      val dest_city = regionDataDest.cityName

      val date = new DateTime((ts.toDouble * 1000).toLong)
      val year = date.getYear
      val month = date.getMonthOfYear
      val day = date.getDayOfMonth
      val hour = date.getHourOfDay
      val minute = date.getMinuteOfHour
      val second = date.getSecondOfMinute
      val milisecond = date.getMillis

      new EventObj(
        ts, company, device_id, year, month, day, hour, minute, second, milisecond,
        protocol, ip_type, src_mac, dest_mac, src_ip, dest_ip,
        src_port, dest_port, alert_msg, classification, priority,
        sig_id, sig_gen, sig_rev, src_country, src_region, src_city, dest_country, dest_region, dest_city
      )
    }.toDF(ColsArtifact.colsEventObj: _*)

    val eventDs = eventDf.select($"ts", $"company", $"device_id", $"year", $"month",
      $"day", $"hour", $"minute", $"second", $"milisecond", $"protocol", $"ip_type",
      $"src_mac", $"dest_mac", $"src_ip", $"dest_ip", $"src_port",
      $"dest_port", $"alert_msg", $"classification", $"priority",
      $"sig_id", $"sig_gen", $"sig_rev", $"src_country", $"src_region", $"src_city",
      $"dest_country", $"dest_region", $"dest_city").as[EventObj]

    val writerEvent = new ForeachWriter[EventObj] {
      override def open(partitionId: Long, version: Long): Boolean = true

      override def process(value: EventObj): Unit = {
        SQLHelper.pushEvent(value)
      }

      override def close(errorOrNull: Throwable): Unit = {}
    }

    eventDs
      .writeStream.queryName("Push to Database")
      .outputMode("update")
      .foreach(writerEvent)
      .start()

    sparkSession.streams.awaitAnyTermination()
  }
}
