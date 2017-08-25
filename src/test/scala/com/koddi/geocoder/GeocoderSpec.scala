/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder
package test

class GeocoderSpec extends TestSpec {

    "A Geocoder" should "lookup a given address and produce a sequence of Results" in {
        val geocoder = new MockGeocoder("api_response_address.xml")
        val results = geocoder.lookup("2821 West 7th St., Dallas, TX 76107, US")
        val location = results.head.geometry.location

        loseAccuracy(location.latitude) should be(33)
        loseAccuracy(location.longitude) should be(-97)
    }

    it should "throw an exception when an invalid address is given" in {
        val geocoder = new MockGeocoder("api_response_invalid.xml")
        an [InvalidLocationException] should be thrownBy {
            geocoder.lookup("Nowhere NONE INVALID ADDRESS")
        }
    }

    it should "reverse lookup lat/lng values and return a sequence of Results" in {
        val geocoder = new MockGeocoder("api_response_latlng.xml")
        val results = geocoder.lookup(32.7505842, -97.3574015)
        val address = results.head.formattedAddress

        address should be("2821 W 7th St, Fort Worth, TX 76107, USA")
    }

    it should "throw an exception when an invalid lat/lng is given" in {
        val geocoder = new MockGeocoder("api_response_invalid.xml")
        an [InvalidLocationException] should be thrownBy {
            geocoder.lookup(-900d, -900d)
        }
    }

    it should "lookup an address by component objects" in {
        val geocoder = new MockGeocoder("api_response_address.xml")
        val results = geocoder.lookup(Seq(
            PostalCodeComponent("76107"),
            CountryComponent("us")
        ))

        val location = results.head.geometry.location

        loseAccuracy(location.latitude) should be(33)
        loseAccuracy(location.longitude) should be(-97)
    }

    private def loseAccuracy(value: Double): Long = Math.round(value)
}

