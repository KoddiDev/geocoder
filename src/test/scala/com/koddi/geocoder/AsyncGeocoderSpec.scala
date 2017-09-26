/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder
package test

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Success,Failure}

class AsyncGeocoderSpec extends TestSpec {

    import scala.concurrent.ExecutionContext.Implicits.global

    "An AsyncGeocoder" should "lookup a given address and trigger a success" in {
        val geocoder = new AsyncGeocoder(new MockGeocoder("api_response_address.xml"))
        val query = geocoder.lookup("2821 West 7th St., Dallas, TX 76107, US")
        query onComplete {
            case Success(results) => {
                val location = results.head.geometry.location
                loseAccuracy(location.latitude) should be(33)
                loseAccuracy(location.longitude) should be(-97)
            }
            case Failure(_) => fail
        }
        Await.ready(query, 5.seconds)
    }

    it should "reverse lookup lat/lng values and trigger a success" in {
        val geocoder = new AsyncGeocoder(new MockGeocoder("api_response_latlng.xml"))
        val query = geocoder.lookup(32.7505842, -97.3574015)
        query onComplete {
            case Success(results) => {
                val address = results.head.formattedAddress
                address should be("2821 W 7th St, Fort Worth, TX 76107, USA")
            }
            case Failure(_) => fail
        }
        Await.ready(query, 5.seconds)
    }

    it should "lookup an address by component objects" in {
        val geocoder = new AsyncGeocoder(new MockGeocoder("api_response_address.xml"))
        val query = geocoder.lookup(Seq(
            PostalCodeComponent("76107"),
            CountryComponent("us")
        ))

        query onComplete {
            case Success(results) => {
                val location = results.head.geometry.location
                loseAccuracy(location.latitude) should be(33)
                loseAccuracy(location.longitude) should be(-97)
            }
            case Failure(_) => fail
        }
    }

    it should "return an empty result sequence when the response has zero results" in {
        val geocoder = new AsyncGeocoder(new MockGeocoder("api_response_zero_results.xml"))
        val query = geocoder.lookup("NONE INVALID ADDRESS")
        query onComplete {
            case Success(results) => results should be(Seq.empty[Result])
            case Failure(error) => fail
        }
        Await.ready(query, 5.seconds)
    }

    it should "trigger a failure when an invalid lat/lng is given" in {
        val geocoder = new AsyncGeocoder(new MockGeocoder("api_response_invalid.xml"))
        val query = geocoder.lookup(-900d, -900d)
        query onComplete {
            case Success(_) => fail
            case Failure(error) => error should not be(null)
        }
        Await.ready(query, 5.seconds)
    }

    private def loseAccuracy(value: Double): Long = Math.round(value)
}

