name := "KaspaCore"

version := "0.5"

scalaVersion := "2.12.11"

assemblyShadeRules in assembly := Seq(
  ShadeRule.rename("com.fasterxml.**" -> "shadeio.@1")
    .inLibrary(
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.1",
      "com.fasterxml.jackson.core" % "jackson-core" % "2.11.1",
      "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.11.1",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.11.1",
      "com.snowplowanalytics" %% "scala-maxmind-iplookups" % "0.7.1",
      "com.maxmind.geoip2" % "geoip2" % "2.14.0",
      "com.maxmind.db" % "maxmind-db" % "1.4.0"
    )
)

resolvers += "confluent" at "https://packages.confluent.io/maven/"
resolvers += "Spark Packages Repo" at "https://dl.bintray.com/spark-packages/maven"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.11.1"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.1"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-scala
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.1"

// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.11.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.6"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.6"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql-kafka-0-10
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.4.6"

// https://mvnrepository.com/artifact/io.confluent/kafka-avro-serializer
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "5.5.1"

// https://mvnrepository.com/artifact/org.apache.spark/spark-avro
libraryDependencies += "org.apache.spark" %% "spark-avro" % "2.4.6"

// https://mvnrepository.com/artifact/org.scalaz/scalaz-core
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.2"

// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.5.0"

// https://mvnrepository.com/artifact/joda-time/joda-time
libraryDependencies += "joda-time" % "joda-time" % "2.10.6"

// https://mvnrepository.com/artifact/com.snowplowanalytics/scala-maxmind-iplookups
libraryDependencies += "com.snowplowanalytics" %% "scala-maxmind-iplookups" % "0.7.1"

// https://mvnrepository.com/artifact/com.maxmind.geoip2/geoip2
libraryDependencies += "com.maxmind.geoip2" % "geoip2" % "2.14.0"

// https://mvnrepository.com/artifact/com.maxmind.db/maxmind-db
libraryDependencies += "com.maxmind.db" % "maxmind-db" % "1.4.0"

libraryDependencies += "com.typesafe" % "config" % "1.4.0"

// https://mvnrepository.com/artifact/org.postgresql/postgresql
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.14"

assemblyMergeStrategy in assembly := {
  {
    case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
  }
}