package com.github.mcross1882.geocoder

case class Location(lat: Double, lng: Double) {

    override def toString(): String = s"${lat},${lng}"
}

case class GeometryBounds(northeast: Location, southwest: Location)

case class Geometry(location: Location, location_type: String, viewport: GeometryBounds, bounds: Option[GeometryBounds])

case class AddressComponent(long_name: String, short_name: String, types: Array[String])

case class MapComponent(
    address_components: Array[AddressComponent],
    formatted_address: String,
    geometry: Geometry,
    partial_match: Boolean,
    place_id: String,
    types: Array[String]
)
 
case class MapsResponse(results: Array[MapComponent], status: String)

