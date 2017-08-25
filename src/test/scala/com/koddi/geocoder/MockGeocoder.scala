/**
  * Copyright (C) 2017-2018 Koddi Inc
  * See the LICENSE file distributed with this work for additional
  * information regarding copyright ownership.
  */
package com.koddi.geocoder
package test

import java.net.URL

class MockGeocoder(filename: String) extends Geocoder("localhost", None, None, new ResponseParser) {

    private val mockParser = new ResponseParser

    override protected def createURL(searchParam: String, searchValue: String): URL = null

    override protected def doGetRequest(url: URL): Response = mockParser.parse(getClass.getResourceAsStream("/" + filename))
}

