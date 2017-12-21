/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder

import java.io.InputStream
import scala.xml.{NodeSeq, XML}
import scala.io.Source

class InvalidResponseException(message: String) extends Exception(message)

/** Parses Google Maps XML responses and converts them into [[com.koddi.geocoder.Response]].
 *
 * @see https://developers.google.com/maps/documentation/geocoding/intro#Results
 */
class ResponseParser {
    /** Parses an input stream containing XML data and converts it to a [[com.koddi.geocoder.Response]].
     *
     * The XML input should be welformed and not contain any undefined entities.
     *
     * @param stream a stream containing XML data
     * @return an immutable Response instance
     */
    def parse(stream: InputStream): Response = {
        val root = XML.load(stream)
        Response(
            text(root, "status"),
            readResults(root \ "result"),
            optional(root, "error_message")
        )
    }

    private def readResults(node: NodeSeq): Seq[Result] = {
        node.map{ result =>
            val types = (result \ "type").map(_.text.trim).toSeq

            Result(
                text(result, "place_id"),
                text(result, "formatted_address"),
                readGeometry(result),
                readAddressComponents(result),
                readPostcodeLocalities(result),
                boolean(result, "partial_match"),
                types
            )
        }
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
        if (bounds.length <= 0) None else Some(readGeometryBounds(node, "bounds"))
    }

    private def readAddressComponents(node: NodeSeq): Seq[AddressComponent] = {
        (node \ "address_component").map{ component =>
            val types = (component \ "type").map(_.text.trim).toSeq

            AddressComponent(
                text(component, "long_name"),
                text(component, "short_name"),
                types
            )
        }
    }

    private def readPostcodeLocalities(node: NodeSeq): Option[Seq[String]] = {
        val postcodes = (node \ "postcode_localities")
        if (postcodes.length <= 0) None else Some(postcodes.map(_.text.trim))
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
        case value: String if !value.isEmpty => Some(value)
        case _ => None
    }
}
