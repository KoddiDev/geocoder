/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder

import java.net.{URL, URLEncoder}
import java.io.InputStream

/** Factory for [[com.koddi.geocoder.Geocoder]] instances. */
object Geocoder {
    val API_URL                  = "https://maps.googleapis.com/maps/api/geocode/xml"
    val API_PARAM_ADDRESS        = "address"
    val API_PARAM_PLACE_ID       = "place_id"
    val API_PARAM_COMPONENTS     = "components"
    val API_PARAM_LANGUAGE       = "language"
    val API_PARAM_REGION         = "region"
    val API_PARAM_RESULT_TYPE    = "result_type"
    val API_PARAM_LOCATION_TYPE  = "location_type"
    val API_PARAM_BOUNDS         = "bounds"
    val API_PARAM_LATLNG         = "latlng"
    val API_PARAM_KEY            = "key"

    /** Creates an anonymous Geocoder without an API key or extra parameters */
    def create(): Geocoder = {
        new Geocoder(API_URL, None, None, new ResponseParser)
    }

    /** Creates an anonymous Geocoder without an API key */
    def create(parameters: Parameters): Geocoder = {
        new Geocoder(API_URL, None, Some(parameters), new ResponseParser)
    }

    /** Create a Geocoder with a given API Key
     *
     * @param key the API key created by the Geo API server
     * @return a new Geocoder instance with requests bound to
     *         the provided api key
     */
    def create(key: String, parameters: Option[Parameters] = None): Geocoder = {
        new Geocoder(API_URL, Some(key), parameters, new ResponseParser)
    }

    def createAsync(): AsyncGeocoder = {
        new AsyncGeocoder(create())
    }

    /** Create an AsyncGeocoder without an API key */
    def createAsync(parameters: Parameters): AsyncGeocoder = {
        new AsyncGeocoder(create(parameters))
    }

    /** Create an AsyncGeocoder with a given API Key
     *
     * @param key the API key created by the Geo API server
     * @return a new AsyncGeocoder instance with requests bound to
     *         the provided api key
     */
    def createAsync(key: String, parameters: Option[Parameters] = None): AsyncGeocoder = {
        new AsyncGeocoder(create(key, parameters))
    }
}

/** Converts strings and addresses to latitude/longitude values.
 *
 * Latitude/Longitude values can be queried with a formatted address.
 * The Google Maps API is called using the values from the address
 * and a [[com.koddi.geocoder.Result]] instance is returned.
 *
 *
 * @param apiUrl the api endpoint used to send requests to
 * @param apiKey an optional key to use when making api requests
 * @param parameters global parameters to apply to every request
 * @param responseParser an XML parser used to deconstruct the API response
 */
class Geocoder(apiUrl: String, apiKey: Option[String], parameters: Option[Parameters], responseParser: ResponseParser) {
    /** Lookups a latitude/longitude values for a given address.
     *
     * A request to the Google Maps API is made to obtain the
     * correct latitude/longitude values.
     *
     * @param address a formatted string containing the address, city, and state
     * @return an sequence of Result objects containing location and geometry data
     */
    def lookup(address: String): Seq[Result] = {
        sendRequest(Geocoder.API_PARAM_ADDRESS, address)
    }

    /** Lookups an address given a location entity.
     *
     * A request to the Google Maps API is made to obtain the
     * correct address value. This is extracted using `Address.fromString`
     * from the `formatted_address` property in the response.
     *
     * @return an sequence of Result objects containing location and geometry data
     */
    def lookup(latitude: Double, longitude: Double): Seq[Result] = {
        sendRequest(Geocoder.API_PARAM_LATLNG, Location(latitude, longitude).toString)
    }

    /** Query the Geocoder API using Component entities.
     *
     * Components represent query parameters and are part of the
     * Google Maps Geocing API. For simplicity predefined Component
     * types are defined in [[com.koddi.geocoder.Component]]
     */
    def lookup(components: Seq[AbstractComponent]): Seq[Result] = {
        val encoded = components.map(_.toString).mkString("|")
        sendRequest(Geocoder.API_PARAM_COMPONENTS, encoded)
    }

    /** Query the Geocoder API using a Place ID
     *
     * @see lookup(address, parameters)
     */
    def lookupPlace(placeId: String): Seq[Result] = {
        sendRequest(Geocoder.API_PARAM_PLACE_ID, placeId)
    }

    protected def sendRequest(searchParam: String, searchValue: String): Seq[Result] = {
        val url = createURL(searchParam, searchValue)
        val response = doGetRequest(url)
        if (!response.success) {
            val message = response.errorMessage match {
                case Some(error) => error
                case None => s"${searchParam.capitalize} '${searchValue}' could not be geocoded."
            }

            response.status match {
                case Response.STATUS_OVER_QUERY_LIMIT => throw new OverQueryLimitException(message)
                case Response.STATUS_INVALID_REQUEST  => throw new InvalidRequestException(message)
                case status => throw new FailedResponseException(status, message)
            }
        }

        if (response.hasNoResults) {
            Seq.empty[Result]
        } else {
            response.results
        }
    }

    protected def createURL(searchParam: String, searchValue: String): URL = {
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
            case _ => // noop
        }

        parameters match {
            case Some(params) => params.appendToUrlBuilder(builder)
            case None => // noop
        }

        new URL(builder.toString)
    }

    // scalastyle:off
    protected def doGetRequest(url: URL): Response = {
        var result: Response = null
        var stream: InputStream = null
        try {
            stream = url.openStream
            result = responseParser.parse(stream)
        } catch {
            case e: Exception => throw new ResponseParsingException(e)
        } finally {
            if (null != stream) {
                stream.close
            }
        }
        result
    }
    // scalastyle:on
}

