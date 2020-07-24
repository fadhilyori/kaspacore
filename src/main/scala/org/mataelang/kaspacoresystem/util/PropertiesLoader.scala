package org.mataelang.kaspacoresystem.util

import com.typesafe.config.{Config, ConfigFactory}


object PropertiesLoader {
  private val conf: Config = ConfigFactory.load("application.conf")

  val kafkaBrokerUrl: String = conf.getString("KAFKA_BROKER_URL")
  val schemaRegistryUrl: String = conf.getString("SCHEMA_REGISTRY_URL")
  val kafkaInputTopic: String = conf.getString("KAFKA_INPUT_TOPIC")
  val kafkaStartingOffset: String = conf.getString("KAFKA_STARTING_OFFSET")
  val kafkaBrokerUrlOutput: String = conf.getString("KAFKA_BROKER_URL_OUTPUT")
  val kafkaOutputTopic: String = conf.getString("KAFKA_OUTPUT_TOPIC")

  val sparkMaster: String = conf.getString("SPARK_MASTER")
  val sparkAppName: String = conf.getString("SPARK_APP_NAME")
  val sparkAppId: String = conf.getString("SPARK_APP_ID")

  val GeoIpPath: String = conf.getString("GEOIP_PATH")
  val GeoIpFilename: String = conf.getString("GEOIP_FILENAME")

  val postgresqlHost: String = conf.getString("POSTGRESQL_HOST")
  val postgresqlPort: Integer = conf.getInt("POSTGRESQL_PORT")
  val postgresqlUsername: String = conf.getString("POSTGRESQL_USERNAME")
  val postgresqlPassword: String = conf.getString("POSTGRESQL_PASSWORD")
  val postgresqlDatabase: String = conf.getString("POSTGRESQL_DATABASE")
}
