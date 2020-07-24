package org.mataelang.kaspacoresystem.util

import cats.effect.IO
import com.snowplowanalytics.maxmind.iplookups.CreateIpLookups
import org.apache.spark.SparkFiles
import org.mataelang.kaspacoresystem.models.Region


object Tools {

  def IpLookupCountry(ipAddress: String): Region = {
    val regionData = new Region()
    val result = (for {
      ipLookups <- CreateIpLookups[IO].createFromFilenames(
        geoFile = Some(SparkFiles.get(PropertiesLoader.GeoIpFilename)),
        ispFile = None,
        domainFile = None,
        connectionTypeFile = None,
        memCache = false,
        lruCacheSize = 20000
      )

      lookup <- ipLookups.performLookups(ipAddress)
    } yield lookup).unsafeRunSync()

    result.ipLocation match {
      case Some(Right(loc)) =>
        if (loc.countryName.isEmpty)
          regionData.countryName = "UNDEFINED"
        else
          regionData.countryName = loc.countryName

        if(loc.regionName.isEmpty)
          regionData.regionName = "UNDEFINED"
        else
          regionData.regionName = loc.regionName.get

        if (loc.city.isEmpty)
          regionData.cityName = "UNDEFINED"
        else
          regionData.cityName = loc.city.get
      case _ =>
        regionData.countryName = "UNDEFINED"
        regionData.regionName = "UNDEFINED"
        regionData.cityName = "UNDEFINED"

    }
    regionData
  }
}
