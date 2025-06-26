package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.GeolocationService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller for handling geolocation-related operations.
 *
 * This controller provides endpoints for:
 * - Fetching current geolocation data
 * - Getting location information by coordinates
 * - Retrieving nearby cities based on coordinates
 *
 * All endpoints return location data in English language format.
 *
 * @see GeolocationService for the underlying geolocation operations
 */
@RestController
@RequestMapping(Paths.GEOLOCATION)
class GeolocationController(
    private val geolocationService: GeolocationService
) {

    /**
     * Fetches the current geolocation information.
     *
     * @return String containing the city name of the current location
     * @apiNote Uses the client's IP address to determine location
     * @example GET /api/geolocation/fetch
     */
    @GetMapping(Paths.FETCH_GEOLOCATION)
    fun fetchGeolocation() = geolocationService.fetchGeolocation()

    /**
     * Gets geolocation information for specified coordinates.
     *
     * @param latitude The latitude coordinate (decimal degrees)
     * @param longitude The longitude coordinate (decimal degrees)
     * @return String containing the city name for the coordinates
     * @apiNote Falls back to "Zagreb" if location cannot be determined
     */
    @GetMapping(Paths.GET_GEOLOCATION)
    fun getGeolocation(
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ) = geolocationService.getGeolocation(latitude, longitude)

    /**
     * Retrieves a list of nearby cities for specified coordinates.
     *
     * @param latitude The latitude coordinate (decimal degrees)
     * @param longitude The longitude coordinate (decimal degrees)
     * @return List of city names within 100km radius, or null if error occurs
     * @apiNote Uses RapidAPI's GeoDB Cities API
     */
    @GetMapping(Paths.GET_NEARBY_CITIES)
    fun getNearbyCities(
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ): List<String>? = geolocationService.getNearbyCities(latitude, longitude)
}
