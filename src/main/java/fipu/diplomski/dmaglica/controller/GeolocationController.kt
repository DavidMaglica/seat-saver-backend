package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.GeolocationService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller for handling geolocation-related operations.
 *
 * This controller provides endpoints for:
 * - Retrieving nearby cities based on coordinates
 * - Get display images from API
 *
 * @see GeolocationService for the underlying geolocation operations
 */
@RestController
class GeolocationController(
    private val geolocationService: GeolocationService
) {

    /**
     * Retrieves a list of nearby cities for specified coordinates.
     *
     * @param latitude The latitude coordinate (decimal degrees)
     * @param longitude The longitude coordinate (decimal degrees)
     * @return List of 10 items (API limit) with city names within 100km radius, minimum population of 10_000 or null if error occurs
     * @apiNote Uses RapidAPI's GeoDB Cities API
     */
    @GetMapping(Paths.GET_NEARBY_CITIES)
    fun getNearbyCities(
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ): List<String>? = geolocationService.getNearbyCities(latitude, longitude)
}
