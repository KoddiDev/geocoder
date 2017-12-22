/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder

/**
 * Thrown when the response contains an invalid payload that cannot be parsed by the XML reader
 */
case class ResponseParsingException(e: Exception) extends Exception(e)

/**
 * Thrown when the API returns a generic error
 */
case class FailedResponseException(status: String, message: String) extends Exception(message)

/**
 * Thrown when the API returns INVALID_REQUEST status. Will contain the applicable error message.
 */
case class InvalidRequestException(message: String) extends Exception(message)

/**
 * Thrown when the API returns OVER_QUERY_LIMIT status. Will contain the applicable error message.
 */
case class OverQueryLimitException(message: String) extends Exception(message)

