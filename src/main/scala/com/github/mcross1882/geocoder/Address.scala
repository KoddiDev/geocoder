package com.github.mcross1882.geocoder

/** Factory to generate Address objects from formatted strings */
object Address {

    private val extractor = """([^,]+),[\s]*([^,]+),[\s]*([^ ,]+)[\s]*(\d+)?[,]?[\s]*(.+)?""".r

    /** Converts a formatted string into an Address object.
     *
     * Regex is used to extract the address values. Zip and country are 
     * optional and will be converted to {{{ None }}} if no match is found.
     *
     * Regex Format:
     * (street), (city), (state)[ (zip)][, (country)]
     *
     * @param raw the formatted address value
     * @return an extracted Address object
     */
    def fromString(raw: String): Address = {
        raw match {
            case extractor(street, city, state, zip, country) => Address(street, city, state, optional(zip), optional(country))
            case _ => throw new IllegalArgumentException(s"'${raw}' could not be parsed into an address entity")
        }
    }

    private def optional(value: String): Option[String] = {
        if (null == value) {
            return None
        }
        Some(value)
    }
}

case class Address(street: String, city: String, state: String, zip: Option[String], country: Option[String]) {

    private val baseAddress = s"${street}, ${city}, ${state}"

    override def toString(): String = {
        val builder = new StringBuilder(baseAddress)
        zip match {
            case Some(z) => builder.append(s" $z")
            case None => // noop
        }

        country match {
            case Some(c) => builder.append(s", $c")
            case None => // noop
        }
        builder.toString
    }
}

