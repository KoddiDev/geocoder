package com.github.mcross1882.geocoder

import java.net.{URL, URLEncoder}
import java.io.InputStream

class InvalidLocationException(message: String) extends Exception(message)

/** Factory for [[com.github.mcross1882.geocoder.Geocoder]] instances. */
object Geocoder {
    val API_URL                  = "https://maps.googleapis.com/maps/api/geocode/xml"
    val API_PARAM_ADDRESS        = "address"
    val API_PARAM_COMPONENTS     = "components"
    val API_PARAM_LANGUAGE       = "language"
    val API_PARAM_REGION         = "region"
    val API_PARAM_RESULT_TYPE    = "result_type"
    val API_PARAM_LOCATION_TYPE  = "location_type"
    val API_PARAM_BOUNDS         = "bounds"
    val API_PARAM_LATLNG         = "latlng"
    val API_PARAM_KEY            = "key"

    /** Creates an anonymous Geocoder without an API key */
    def create(): Geocoder = new Geocoder(API_URL, None, new ResponseParser)

    /** Create a Geocoder with a given API Key
     *
     * @param key the API key created by the Geo API server
     * @return a new Geocoder instance with requests bound to
     *         the provided api key
     */
    def create(key: String): Geocoder = new Geocoder(API_URL, Some(key), new ResponseParser)

    /** Create an AsyncGeocoder without an API key */
    def createAsync(): AsyncGeocoder = new AsyncGeocoder(create)

    /** Create an AsyncGeocoder with a given API Key
     *
     * @param key the API key created by the Geo API server
     * @return a new AsyncGeocoder instance with requests bound to
     *         the provided api key
     */
    def createAsync(key: String): AsyncGeocoder = new AsyncGeocoder(create(key))
}

/** Converts strings and addresses to latitude/longitude values.
 *
 * Latitude/Longitude values can be queried with a formatted address.
 * The Google Maps API is called using the values from the address 
 * and a [[com.github.mcross1882.geocoder.Result]] instance is returned.
 *
 *
 * @param apiUrl the api endpoint used to send requests to
 * @param apiKey an optional key to use when making api requests
 * @param responseParser an XML parser used to deconstruct the API response
 */
class Geocoder(apiUrl: String, apiKey: Option[String], responseParser: ResponseParser) {
    /** Lookups a latitude/longitude values for a given address.
     *
     * A request to the Google Maps API is made to obtain the
     * correct latitude/longitude values.
     *
     * @param address a formatted string containing the address, city, and state
     * @return an sequence of Result objects containing location and geometry data
     */
    def lookup(address: String, params: Parameters = Parameters()): Seq[Result] = {
        sendRequest(Geocoder.API_PARAM_ADDRESS, address, params)
    }

    def lookup(placeId: String, params: Parameters = Parameters()): Seq[Result] = {
        sendRequest(Geocoder.API_PARAM_PLACE_ID, placeId, params)
    }

    /** Lookups an address given a location entity.
     *
     * A request to the Google Maps API is made to obtain the
     * correct address value. This is extracted using {{{ Address.fromString }}}
     * from the {{{ formatted_address }}} property in the response.
     *
     * @return an sequence of Result objects containing location and geometry data
     */
    def lookup(latitude: Double, longitude: Double, params: Parameters = Parameters()): Seq[Result] = {
        sendRequest(Geocoder.API_PARAM_LATLNG, Location(latitude, longitude).toString, params)
    }

    def lookup(components: Seq[Component], params: Parameters = Parameters()): Seq[Result] = {
        val encoded = components.map(_.toString).mkString("|")
        sendRequest(Geocoder.API_PARAM_COMPONENTS, encoded, params)
    }

    private def sendRequest(searchParam: String, searchValue: String, params: Parameters): Seq[Result] = {
        val url = createURL(searchParam, searchValue, params)
        val response = doGetRequest(url)
        if (!response.success) {
            val message = response.errorMessage match {
                case Some(error) => error
                case None => s"${searchParam.capitalize} '${searchValue}' could not be geocoded."
            }
            throw new InvalidLocationException(message)
        }

        if (response.hasNoResults) {
            return Seq.empty[Result]
        }
        response.results
    }

    private def createURL(searchParam: String, searchValue: String, params: Parameters): URL = {
        val builder = new StringBuilder(apiUrl)
        builder.append("?")
        builder.append(searchParam)
        builder.append("=")
        builder.append(URLEncoder.encode(searchValue, "UTF-8"))
        apiKey match {
            case Some(key) => {
                builder.append("&")
                builder.append(Geocoder.API_PARAM_KEY)
                builder.append("=")
                builder.append(URLEncoder.encode(key, "UTF-8"))
            }
        }

        params.appendToUrlBuilder(builder)

        new URL(builder.toString)
    }

    private def doGetRequest(url: URL): Response = {
        var result: Response = null
        var stream: InputStream = null
        try {
            stream = url.openStream
            result = responseParser.parse(stream)
        } finally {
            if (null != stream) {
                stream.close
            }
        }
        result
    }
}

