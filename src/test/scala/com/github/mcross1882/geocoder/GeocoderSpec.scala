package com.github.mcross1882.geocoder
package test

class GeocoderSpec extends TestSpec {

    private val geocoder = Geocoder.create

    "A Geocoder" should "lookup a given address and convert it to a location entity" in {
        val location = geocoder.lookup(
            Address("2821 West 7th St.", "Dallas", "TX", Some("76107"), Some("US"))
        ).get

        loseAccuracy(location.lat) should be(33)
        loseAccuracy(location.lng) should be(-97)
    }

    it should "lookup a string based address and convert it to a location entity" in {
        val location = geocoder.lookup("2821 West 7th St., Dallas, TX 76107, US").get

        loseAccuracy(location.lat) should be(33)
        loseAccuracy(location.lng) should be(-97)
    }

    it should "lookup an address without a zip code" in {
        val location = geocoder.lookup(
            Address("2821 West 7th St.", "Dallas", "TX", None, Some("US"))
        ).get

        loseAccuracy(location.lat) should be(33)
        loseAccuracy(location.lng) should be(-97)
    }

    it should "lookup an address without a country" in {
        val location = geocoder.lookup(
            Address("2821 West 7th St.", "Dallas", "TX", Some("76107"), None)
        ).get

        loseAccuracy(location.lat) should be(33)
        loseAccuracy(location.lng) should be(-97)
    }

    it should "lookup an address without a zip code or country" in {
        val location = geocoder.lookup(
            Address("2821 West 7th St.", "Dallas", "TX", None, None)
        ).get

        loseAccuracy(location.lat) should be(33)
        loseAccuracy(location.lng) should be(-97)
    }

    it should "throw an exception when an invalid address is given" in {
        an [InvalidLocationException] should be thrownBy {
            geocoder.lookup(Address("Nowhere", "DOES NOT EXIST", "", None, None))
        }
    }

    it should "reverse lookup a location and return an address entity" in {
        val address = geocoder.reverseLookup(Location(32.7505842, -97.3574015)).get

        address.street should be("2821 W 7th St")
        address.city should be("Fort Worth")
        address.state should be("TX")
        address.zip should be(Some("76107"))
        address.country should be(Some("USA"))
    }

    it should "reverse lookup lat/lng values and return an address entity" in {
        val address = geocoder.reverseLookup(32.7505842, -97.3574015).get

        address.street should be("2821 W 7th St")
        address.city should be("Fort Worth")
        address.state should be("TX")
        address.zip should be(Some("76107"))
        address.country should be(Some("USA"))
    }

    it should "throw an exception when an invalid lat/lng is given" in {
        an [InvalidLocationException] should be thrownBy {
            geocoder.reverseLookup(Location(-900d, -900d))
        }
    }

    private def loseAccuracy(value: Double): Long = Math.round(value)
}

