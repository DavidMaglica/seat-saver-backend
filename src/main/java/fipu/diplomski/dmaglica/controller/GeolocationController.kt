package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.service.GeolocationService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Paths.GEOLOCATION)
class GeolocationController(
    private val geolocationService: GeolocationService
) {

    @GetMapping(Paths.FETCH_GEOLOCATION)
    fun fetchGeolocation() = geolocationService.fetchGeolocation()

    @GetMapping(Paths.GET_GEOLOCATION)
    fun getGeolocation(
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ) = geolocationService.getGeolocation(latitude, longitude)

    @GetMapping(Paths.GET_NEARBY_CITIES)
    fun getNearbyCities(
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ): List<String>? = geolocationService.getNearbyCities(latitude, longitude)

}