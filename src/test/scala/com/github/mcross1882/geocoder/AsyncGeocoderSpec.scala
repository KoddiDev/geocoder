package com.github.mcross1882.geocoder
package test

import scala.concurrent.Await
import scala.concurrent.duration._

class AsyncGeocoderSpec extends TestSpec {

    import scala.concurrent.ExecutionContext.Implicits.global

    private val geocoder = Geocoder.createAsync

    "An AsyncGeocoder" should "lookup a given address and convert it to a location entity" in {
        val query = geocoder.lookup("2821 West 7th St., Dallas, TX 76107, US")
        query onSuccess { case results =>
            val location = results.head.geometry.location
            loseAccuracy(location.latitude) should be(33)
            loseAccuracy(location.longitude) should be(-97)
        }
    }

    it should "lookup an address without a zip code" in {
        val results = geocoder.lookup("2821 West 7th St., Dallas, TX, US")
        val query = geocoder.lookup("2821 West 7th St., Dallas, TX, US")
        query onSuccess { case results =>
            val location = results.head.geometry.location
            loseAccuracy(location.latitude) should be(33)
            loseAccuracy(location.longitude) should be(-97)
        }
    }

    it should "lookup an address without a country" in {
        val query = geocoder.lookup("2821 West 7th St., Dallas, TX 76107")
        query onSuccess { case results =>
            val location = results.head.geometry.location
            loseAccuracy(location.latitude) should be(33)
            loseAccuracy(location.longitude) should be(-97)
        }
    }

    it should "lookup an address without a zip code or country" in {
        val query = geocoder.lookup("2821 West 7th St., Dallas, TX")
        query onSuccess { case results =>
            val location = results.head.geometry.location
            loseAccuracy(location.latitude) should be(33)
            loseAccuracy(location.longitude) should be(-97)
        }
    }

    it should "throw an exception when an invalid address is given" in {
        geocoder.lookup("Nowhere NONE INVALID ADDRESS") onFailure {
            case error => error should not be(null)
        }
    }

    it should "reverse lookup lat/lng values and return an address entity" in {
        val query = geocoder.reverseLookup(32.7505842, -97.3574015)
        query onSuccess { case results =>
            val address = results.head.formattedAddress
            address should be("2821 W 7th St, Fort Worth, TX 76107, USA")
        }
    }

    it should "throw an exception when an invalid lat/lng is given" in {
        geocoder.reverseLookup(-900d, -900d) onFailure {
            case error => error should not be(null)
        }
    }

    private def loseAccuracy(value: Double): Long = Math.round(value)
}

