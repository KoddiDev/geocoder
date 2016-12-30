package com.github.mcross1882.geocoder

import scalaj.http.{Http, HttpRequest}
import com.owlike.genson.defaultGenson.{toJson, fromJson}

class InvalidLocationException(message: String) extends Exception(message)

/** Factory for [[com.github.mcross1882.geocoder.Geocoder]] instances. */
object Geocoder {
    private val API_URL           = "https://maps.googleapis.com/maps/api/geocode/json"
    private val API_PARAM_ADDRESS = "address"
    private val API_PARAM_LATLNG  = "latlng"
    private val API_PARAM_KEY     = "key"
    private val API_SUCCESS       = "OK"

    /** Creates an anonymous Geocoder without an API key
     */
    def create(): Geocoder = new Geocoder(API_URL, None)

    /** Create a Geocoder with a given API Key
     *
     * @param key the API key created by the Geo API server
     * @return a new Geocoder instance with requests bound to
     *         the provided api key
     */
    def create(key: String): Geocoder = new Geocoder(API_URL, Some(key))
}

/** Converts strings and addresses to latitude/longitude values.
 *
 * Latitude/Longitude values can be queried either with a raw string
 * or an Address case class. If a raw string is provided then the
 * [[com.github.mcross1882.geocoder.Address]] "fromString" method will be
 * used to construct the Address object. The Google Maps API is called
 * using the values from the address object and a Location object
 * containing the latitude and longitude are returned.
 *
 * Reverse lookups are also supported. These work in an identical manner
 * to Address -> Location lookups but the request is reversed.
 *
 * @param apiUrl the api endpoint used to send requests to
 * @param apiKey an optional key to use when making api requests
 */
class Geocoder(apiUrl: String, apiKey: Option[String]) {
    /** Lookups a latitude/longitude values for a formatted address string.
     *
     * This calls {{{ Address.fromString(text) }}} to obtain the Address object.
     *
     * @param address a formatted string containing the address, city, and state
     * @return a location object containing the latitude/longitude values
     */
    def lookup(address: String): Location = {
        lookup(Address.fromString(address))
    }

    /** Lookups a latitude/longitude values for a given address.
     *
     * A request to the Google Maps API is made to obtain the
     * correct latitude/longitude values.
     *
     * @param address the address to query
     * @return a location object containing the latitude/longitude values
     */
    def lookup(address: Address): Location = {
        val response = sendRequest(Geocoder.API_PARAM_ADDRESS, address.toString)
        response.geometry.location
    }

    /** Lookups an address for a latitude/longitude pair.
     *
     * @return an address object containing the street, city, and state
     */
    def reverseLookup(latitude: Double, longitude: Double): Address = {
        reverseLookup(Location(latitude, longitude))
    }

    /** Lookups an address given a location entity.
     *
     * A request to the Google Maps API is made to obtain the
     * correct address value. This is extracted using {{{ Address.fromString }}}
     * from the {{{ formatted_address }}} property in the response.
     *
     * @param location a latitude/longitude pair to query
     * @return an address object containing the street, city, and state
     */
    def reverseLookup(location: Location): Address = {
        val response = sendRequest(Geocoder.API_PARAM_LATLNG, location.toString)
        Address.fromString(response.formatted_address)
    }

    private def sendRequest(searchParam: String, searchValue: String): MapComponent = {
        val request = buildRequest(searchParam, searchValue)
        val response = fromJson[MapsResponse](request.asString.body)
        if (Geocoder.API_SUCCESS != response.status) {
            throw new InvalidLocationException(s"${searchParam.capitalize} '${searchValue}' could not be geocoded.")
        }

        val results = response.results
        if (results.length <= 0) {
            throw new IllegalStateException(s"No results were returned from the API.")
        }
        results.head
    }

    private def buildRequest(searchParam: String, searchValue: String): HttpRequest = {
        val request = Http(apiUrl).param(searchParam, searchValue)
        apiKey match {
            case Some(key) => request.param(Geocoder.API_PARAM_KEY, key)
            case None => // noop
        }
        request
    }
}

