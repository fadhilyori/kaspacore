package org.mataelang.kaspacoresystem.models

class Region {

  var countryName: String = ""
  var regionName: String = ""
  var cityName: String = ""

  def Region(): Unit = {

  }

  def Region(countryName: String, regionName: String, cityName: String): Unit = {
    this.countryName = countryName
    this.regionName = regionName
    this.cityName = cityName
  }

}
