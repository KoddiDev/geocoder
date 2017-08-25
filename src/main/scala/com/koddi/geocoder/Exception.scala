/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder

class ResponseParsingException(e: Exception) extends Exception(e)

class InvalidLocationException(message: String) extends Exception(message)

