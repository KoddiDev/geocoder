/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder

import org.xml.sax.SAXParseException

class ResponseParserSpec extends TestSpec {

  private val parser = new ResponseParser

  "A ResponseParser" should "parse a valid Maps XML response" in {
    val stream = getClass.getResourceAsStream("/api_response.xml")

    val response = parser.parse(stream)
    stream.close

    response.status should be("OK")
    response.results.length should be > 0

    val result = response.results.head

    result.types should contain("street_address")
    result.addressComponents.head.longName should be("1600")
    result.addressComponents.head.types should be(Seq("street_number"))
    result.addressComponents(2).types should be(Seq("locality", "political"))
    result.geometry.locationType should be("ROOFTOP")
    result.geometry.viewport.southwest.latitude should be(37.4188514)
    result.placeId should be("ChIJ2eUgeAK6j4ARbn5u_wAGqWA")
  }

  it should "parse a valid Maps XML response with multiple result types" in {
    val stream = getClass.getResourceAsStream("/api_response_multiple_result_types.xml")

    val response = parser.parse(stream)
    stream.close

    response.status should be("OK")
    response.results.length should be > 0

    val result = response.results.head

    result.types should be(Seq("street_address", "point_of_interest"))
    result.addressComponents.head.longName should be("1600")
    result.addressComponents.head.types should be(Seq("street_number"))
    result.addressComponents(2).types should be(Seq("locality", "political"))
    result.geometry.locationType should be("ROOFTOP")
    result.geometry.viewport.southwest.latitude should be(37.4188514)
    result.placeId should be("ChIJ2eUgeAK6j4ARbn5u_wAGqWA")
  }

  it should "parse XML responses with postcode localities" in {
    val stream = getClass.getResourceAsStream("/api_response_with_postcodes.xml")

    val response = parser.parse(stream)
    stream.close

    response.status should be("OK")
    response.results.length should be > 0

    val result = response.results.head

    result.types should contain("street_address")
    result.addressComponents.head.longName should be("1600")
    result.geometry.locationType should be("ROOFTOP")
    result.geometry.viewport.southwest.latitude should be(37.4188514)
    result.placeId should be("ChIJ2eUgeAK6j4ARbn5u_wAGqWA")
    result.postcodeLocalities.get should contain("US")
    result.postcodeLocalities.get should contain("US")
  }

  it should "throw an exception when reading an empty file" in {
    val stream = getClass.getResourceAsStream("/api_response_empty.xml")

    try {
      an[SAXParseException] should be thrownBy {
        parser.parse(stream)
      }
    } finally {
      stream.close
    }
  }
}
