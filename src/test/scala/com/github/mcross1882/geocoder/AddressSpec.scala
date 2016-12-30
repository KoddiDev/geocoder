package com.github.mcross1882.geocoder
package test

class AddressSpec extends TestSpec {

    "An AddressSpec" should "parse an address string" in {
        val expected = Address("2821 West 7th St.", "Dallas", "TX", Some("76107"), Some("US"))
        val actual = Address.fromString("2821 West 7th St., Dallas, TX 76107, US")
        actual.toString should be(expected.toString)
    }

    it should "parse an address without a zip code" in {
        val expected = Address("2821 West 7th St.", "Dallas", "TX", None, Some("US"))
        val actual = Address.fromString("2821 West 7th St., Dallas, TX, US")
        actual.toString should be(expected.toString)
    }

    it should "parse an address without a country" in {
        val expected = Address("2821 West 7th St.", "Dallas", "TX", Some("76107"), None)
        val actual = Address.fromString("2821 West 7th St., Dallas, TX 76107")
        actual.toString should be(expected.toString)
    }

    it should "parse an address without a zip code or country" in {
        val expected = Address("2821 West 7th St.", "Dallas", "TX", None, None)
        val actual = Address.fromString("2821 West 7th St., Dallas, TX")
        actual.toString should be(expected.toString)
    }

    it should "throw an exception when parsing an invalid address" in {
        an [IllegalArgumentException] should be thrownBy {
            Address.fromString("Nope")
        }
    }
}

