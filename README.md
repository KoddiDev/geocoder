Geocoder
========

A Google Maps geocoding library for Scala. The goal of the library is provide lightweight, easy to use geocoding functions that are thoroughly tested.

### Building

To build simply run the following `sbt` commands.

```
$ sbt clean compile test doc assembly
```

*API Documentation will be generated in `target/scala-2.11/api/`*

### Usage

To create a `Geocoder` simply call the `Geocoder.create` function.

```scala
// Bring the Geocoder into scope
import com.github.mcross1882.geocoder.Geocoder

// The factory object can be used to lazily create Geocoders
val geo = Geocoder.create

// If you need to use an API key because of limiting
val geoWithKey = Geocoder.create(MY_KEY)

// And lastly if you need to manually create the Geocoder
// that's supported as well.
val customGeo = new Geocoder(API_URL, Some(API_KEY))
```

To perform latitude/longitude lookups simply provide a formatted address.

```scala
// Lookup a location with a formatted address string
val location = geo.lookup("2821 W 7th St, Fort Worth, TX")

// If you need more granular control then the address object can be used
val location = geo.lookup(
    Address("2821 W 7th St", "Fort Worth", "TX", None, None)
)

println(s"Latitude: ${location.lat}, Longitude: ${location.lng}")
```

Performing reverse lookups is just as easy.

```scala
// Lookup an address by latitude/longitude
val address = geo.reverseLookup(32.857, -96.748)

println(address.toString)
```

### Other Related Projects

These projects acheive the same purpose as this library and provide alternative options if this library is unsuitable.

- [NewMotion/scala-geocode](https://github.com/NewMotion/scala-geocode)
- [divarvel/geocoding-api](https://github.com/divarvel/geocoding-api)

