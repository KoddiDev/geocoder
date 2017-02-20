package com.koddi.geocoder

import scala.concurrent.{ExecutionContext, Future}

/** Use the geocoding API with asynchonous requests
 *
 * Simple composition class that executes the Geocoder methods within and
 * implicit {{{ scala.concurrent.ExecutionContext }}}. Exceptions are automatically
 * bubbled up to the {{{ onFailure }}} events. All {{{ scala.concurrent.Future[Result] }}}
 * contain the return values from the wrapper [[com.koddi.geocoder.Geocoder]].
 *
 * @param geo a Geocoder instance to wrap with async callers
 */
class AsyncGeocoder(geo: Geocoder) {

    type FutureResult = Future[Seq[Result]]

    /** Perform an address lookup that returns a {{{ scala.concurrent.Future[Result] }}};
     *
     * @see [[com.koddi.geocoder.Geocoder]]
     */
    def lookup(address: String)
        (implicit context: ExecutionContext): FutureResult = Future {
        geo.lookup(address)
    }(context)

    /** Perform a latitude/longitude lookup that returns a {{{ scala.concurrent.Future[Result] }}}.
     *
     * @see [[com.koddi.geocoder.Geocoder]]
     */
    def lookup(latitude: Double, longitude: Double)
        (implicit context: ExecutionContext): FutureResult = Future {
        geo.lookup(latitude, longitude)
    }(context)

    /** Perform a component lookup that returns a {{{ scala.concurrent.Future[Result] }}}.
     *
     * @see [[com.koddi.geocoder.Geocoder]]
     */
    def lookup(components: Seq[AbstractComponent])
        (implicit context: ExecutionContext): FutureResult = Future {
        geo.lookup(components)
    }(context)

    /** Perform an place id lookup that returns a {{{ scala.concurrent.Future[Result] }}};
     *
     * @see [[com.koddi.geocoder.Geocoder]]
     */
    def lookupPlace(placeId: String)
        (implicit context: ExecutionContext): FutureResult = Future {
        geo.lookupPlace(placeId)
    }(context)
}

