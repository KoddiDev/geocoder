package com.github.mcross1882.geocoder
package test

class GeocoderSpec extends TestSpec {

    private val geocoder = Geocoder.create

    "A Geocoder" should "lookup a given address and produce a sequence of Results" in {
        val results = geocoder.lookup("2821 West 7th St., Dallas, TX 76107, US")
        val location = results.head.geometry.location

        loseAccuracy(location.latitude) should be(33)
        loseAccuracy(location.longitude) should be(-97)
    }

    it should "lookup an address without a zip code" in {
        val results = geocoder.lookup("2821 West 7th St., Dallas, TX, US")
        val location = results.head.geometry.location

        loseAccuracy(location.latitude) should be(33)
        loseAccuracy(location.longitude) should be(-97)
    }

    it should "lookup an address without a country" in {
        val results = geocoder.lookup("2821 West 7th St., Dallas, TX 76107")
        val location = results.head.geometry.location

        loseAccuracy(location.latitude) should be(33)
        loseAccuracy(location.longitude) should be(-97)
    }

    it should "lookup an address without a zip code or country" in {
        val results = geocoder.lookup("2821 West 7th St., Dallas, TX")
        val location = results.head.geometry.location

        loseAccuracy(location.latitude) should be(33)
        loseAccuracy(location.longitude) should be(-97)
    }

    it should "throw an exception when an invalid address is given" in {
        an [InvalidLocationException] should be thrownBy {
            geocoder.lookup("Nowhere NONE INVALID ADDRESS")
        }
    }

    it should "reverse lookup lat/lng values and return a sequence of Results" in {
        val results = geocoder.lookup(32.7505842, -97.3574015)
        val address = results.head.formattedAddress

        address should be("2821 W 7th St, Fort Worth, TX 76107, USA")
    }

    it should "throw an exception when an invalid lat/lng is given" in {
        an [InvalidLocationException] should be thrownBy {
            geocoder.lookup(-900d, -900d)
        }
    }

    private def loseAccuracy(value: Double): Long = Math.round(value)
}

