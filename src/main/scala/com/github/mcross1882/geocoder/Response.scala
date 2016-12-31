package com.github.mcross1882.geocoder

/** Provides constants related to [[com.github.mcross1882.geocoder.Location]] */
object Location {
    val TYPE_ROOFTOP            = "ROOFTOP"
    val TYPE_RANGE_INTERPOLATED = "RANGE_INTERPOLATED"
    val GEOMETRIC_CENTER        = "GEOMETRIC_CENTER"
    val APPROXIMATE             = "APPROXIMATE"
}

/** Simple wrapper for storing latitude and longitude values
 *
 * Internally the {{{ toString() }}} method is overriden
 * so it can write the latitude/longitude values as a
 * coordinate string.
 *
 * @param lat a floating point number representing the latitude
 * @param lng a floating point number representing the longitude
 */
case class Location(latitude: Double, longitude: Double) {

    override def toString(): String = s"${lat},${lng}"
}

/** Bounding points the represent opposite edges of a square.
 *
 * @param northeast the upper right coordinate of the square
 * @param southwest the lower left coordinate of the square
 */
case class GeometryBounds(northeast: Location, southwest: Location)

/** High level geometry data related to the queried location.
 */
case class Geometry(location: Location, locationType: String, viewport: GeometryBounds, bounds: Option[GeometryBounds])

/** Provides constants for [[com.github.mcross1882.geocoder.AddressComponent]] */
object AddressComponent {
    val STREET_ADDRESS              = "street_address"
    val ROUTE                       = "route"
    val INTERSECTION                = "intersection"
    val POLITICAL                   = "political"
    val COUNTRY                     = "country"
    val ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1"
    val ADMINISTRATIVE_AREA_LEVEL_2 = "administrative_area_level_2"
    val ADMINISTRATIVE_AREA_LEVEL_3 = "administrative_area_level_3"
    val ADMINISTRATIVE_AREA_LEVEL_4 = "administrative_area_level_4"
    val ADMINISTRATIVE_AREA_LEVEL_5 = "administrative_area_level_5"
    val COLLOQUIAL_AREA             = "colloquial_area"
    val LOCALITY                    = "locality"
    val WARD                        = "ward"
    val SUBLOCALITY                 = "sublocality"
    val NEIGHBORHOOD                = "neighborhood"
    val PREMISE                     = "premise"
    val SUBPREMISE                  = "subpremise"
    val POSTAL_CODE                 = "postal_code"
    val NATURAL_FEATURE             = "natural_feature"
    val AIRPORT                     = "airport"
    val PARK                        = "park"
    val POINT_OF_INTEREST           = "point_of_interest"
    val FLOOR                       = "floor"
    val ESTABLISHMENT               = "establishment"
    val PARKING                     = "parking"
    val POST_BOX                    = "post_box"
    val POSTAL_TOWN                 = "postal_town"
    val ROOM                        = "room"
    val STREET_NUMBER               = "street_number"
    val BUS_STATION                 = "bus_station"
    val TRAIN_STATION               = "train_station"
    val TRANSIT_STATION             = "transit_station"
}

/** Contains meta data about the address.
 *
 * AddressComponents can contain any part of the address field.
 * This includes street, city, state, etc.. the field type can
 * be determined by the {{{ types }}} array.
 */
case class AddressComponent(longName: String, shortName: String, types: Array[String])

/** A collection of location and geometry data. */
case class MapComponent(
    placeId: String,
    formattedAddress: String,
    geometry: Geometry,
    addressComponents: Array[AddressComponent],
    postcodeLocalities: Option[Array[String]],
    partialMatch: Boolean,
    types: Array[String]
)

/** The Google Maps response wrapper
 *
 * This entity represents the JSON response returned from
 * the Google Maps API.
 */ 
case class MapResults(status: String, components: Array[MapComponent], errorMessage: Option[String])

