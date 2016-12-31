package com.github.mcross1882.geocoder

import java.io.InputStream
import scala.xml.{NodeSeq, XML}

class InvalidResponseException(message: String) extends Exception(message)

/** Parses Google Maps XML responses and converts them into [[com.github.mcross1882.geocoder.MapResults]].
 *
 * @see https://developers.google.com/maps/documentation/geocoding/intro#Results
 */
class ResponseParser {
    /** Parses an input stream containing XML data and converts it to a [[com.github.mcross1882.geocoder.MapResults]].
     *
     * The XML input should be welformed and not contain any undefined entities.
     *
     * @param stream a stream containing XML data
     * @return an immutable MapResults instance
     */ 
    def parse(stream: InputStream): MapResults = {
        val root = XML.load(stream)
        MapResults(
            text(root, "status"),
            readResults(root \ "result"),
            optional(root, "error_message")
        )
    }

    private def readResults(node: NodeSeq): Array[MapComponent] = {
        node.map{ result =>
            MapComponent(
                text(result, "place_id"),
                text(result, "formatted_address"),
                readGeometry(result),
                readAddressComponents(result),
                readPostcodeLocalities(result),
                boolean(result, "partial_match"),
                Array(text(result, "type"))
            )
        }.toArray
    }

    private def readGeometry(node: NodeSeq): Geometry = {
        val geometry = (node \ "geometry")
        Geometry(
            readLocation(geometry, "location"),
            text(geometry, "location_type"),
            readGeometryBounds(geometry, "viewport"),
            readBounds(geometry)
        )
    }

    private def readBounds(node: NodeSeq): Option[GeometryBounds] = {
        val bounds = (node \ "bounds")
        if (bounds.length <= 0) {
            return None
        }
        Some(readGeometryBounds(node, "bounds"))
    }

    private def readAddressComponents(node: NodeSeq): Array[AddressComponent] = {
        (node \ "address_component").map{ component =>
            AddressComponent(
                text(component, "long_name"),
                text(component, "short_name"),
                Array(text(component, "type"))
            )
        }.toArray
    }

    private def readPostcodeLocalities(node: NodeSeq): Option[Array[String]] = {
        val postcodes = (node \ "postcode_localities")
        if (postcodes.length <= 0) {
            return None
        }
        Some(postcodes.map(_.text.trim).toArray)
    }

    private def readGeometryBounds(node: NodeSeq, key: String): GeometryBounds = {
        val bounds = (node \ key)
        GeometryBounds(
            readLocation(bounds, "northeast"),
            readLocation(bounds, "southwest")
        )
    }

    private def readLocation(node: NodeSeq, key: String): Location = {
        val locationNode = (node \ key)
        Location(
            double(locationNode, "lat"),
            double(locationNode, "lng")
        )
    }

    private def boolean(node: NodeSeq, key: String): Boolean = try {
        text(node, key).toBoolean
    } catch {
        case e: Exception => false
    }

    private def double(node: NodeSeq, key: String): Double = try {
        text(node, key).toDouble
    } catch {
        case e: Exception => 0d
    }

    private def text(node: NodeSeq, key: String): String = (node \ key).text.trim

    private def optional(node: NodeSeq, key: String): Option[String] = (node \ key).text.trim match {
        case value if !value.isEmpty => Some(value)
        case _ => None
    }
}
