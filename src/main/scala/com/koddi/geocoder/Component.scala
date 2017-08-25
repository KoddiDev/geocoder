/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder

import java.net.URLEncoder

object Component {
    val ROUTE               = "route"
    val LOCALITY            = "locality"
    val ADMINISTRATIVE_AREA = "administrative_area"
    val POSTAL_CODE         = "postal_code"
    val COUNTRY             = "country"
}

sealed abstract class AbstractComponent(key: String, value: String) {

    private val formattedString = s"${key}:${value}"

    override def toString(): String = formattedString
}

case class Component(key: String, value: String) extends AbstractComponent(key, value)

/** Serializes to "route=value" */
case class RouteComponent(value: String) extends AbstractComponent(Component.ROUTE, value)

/** Serializes to "locality=value" */
case class LocalityComponent(value: String) extends AbstractComponent(Component.LOCALITY, value)

/** Serializes to "administrative_area=value" */
case class AdministrativeAreaComponent(value: String) extends AbstractComponent(Component.ADMINISTRATIVE_AREA, value)

/** Serializes to "postal_code=value" */
case class PostalCodeComponent(value: String) extends AbstractComponent(Component.POSTAL_CODE, value)

/** Serializes to "country=value" */
case class CountryComponent(value: String) extends AbstractComponent(Component.COUNTRY, value)

case class Parameters(
    language: Option[String] = None,
    region: Option[String] = None,
    bounds: Option[GeometryBounds] = None,
    resultType: Option[Seq[String]] = None,
    locationType: Option[Seq[String]] = None) {

    def appendToUrlBuilder(builder: StringBuilder) {
        language match {
            case Some(value) => appendQueryParameter(builder, Geocoder.API_PARAM_LANGUAGE, value)
            case None => // default
        }

        region match {
            case Some(value) => appendQueryParameter(builder, Geocoder.API_PARAM_REGION, value)
            case None => // default
        }

        bounds match {
            case Some(value) => appendQueryParameter(builder, Geocoder.API_PARAM_BOUNDS, value.toString)
            case None => // default
        }

        resultType match {
            case Some(value) => appendQueryParameter(builder, Geocoder.API_PARAM_RESULT_TYPE, value.mkString("|"))
            case None => // default
        }

        locationType match {
            case Some(value) => appendQueryParameter(builder, Geocoder.API_PARAM_LOCATION_TYPE, value.mkString("|"))
            case None => // default
        }
    }

    private def appendQueryParameter(builder: StringBuilder, key: String, value: String) {
        builder.append("&")
        builder.append(URLEncoder.encode(key, "UTF-8"))
        builder.append("=")
        builder.append(URLEncoder.encode(value, "UTF-8"))
    }
}
