package com.github.mcross1882.geocoder

import scala.concurrent.{ExecutionContext, Future}

/** Use the geocoding API with asynchonous requests
 *
 * Simple composition class that executes the Geocoder methods within and
 * implicit {{{ scala.concurrent.ExecutionContext }}}. Exceptions are automatically
 * bubbled up to the {{{ onFailure }}} events. All {{{ scala.concurrent.Future }}}
 * contain the return values from the wrapper [[com.github.mcross1882.geocoder.Geocoder]].
 *
 * @param geo a Geocoder instance to wrap with async callers
 */
class AsyncGeocoder(geo: Geocoder) {

    type FutureResult = Future[Seq[Result]]

    /** Perform an address lookup that returns a {{{ scala.concurrent.Future }}};
     *
     * @see [[com.github.mcross1882.geocoder.Geocoder]]
     */
    def lookup(address: String)(implicit context: ExecutionContext): FutureResult = Future {
        geo.lookup(address)
    }(context)

    /** Perform a latitude/longitude lookup that returns a {{{ scala.concurrent.Future }}}.
     *
     * @see [[com.github.mcross1882.geocoder.Geocoder]]
     */
    def reverseLookup(latitude: Double, longitude: Double)(implicit context: ExecutionContext): FutureResult = Future {
        geo.reverseLookup(latitude, longitude)
    }(context)
}

