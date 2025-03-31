package fipu.diplomski.dmaglica.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class GeolocationService(
    private val restTemplate: RestTemplate
) {

    fun fetchGeolocation(): String {
        val url = "https://api-bdc.net/data/reverse-geocode-client"

        val response = restTemplate.getForObject(url, Map::class.java)?.let {
            return it["city"] as String
        }
        return response ?: "No city found"
    }

    fun getGeolocation(latitude: Double, longitude: Double): String {
        val baseUrl = "https://api-bdc.net/data/reverse-geocode-client"
        val uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("localityLanguage", "en")
            .build()
            .toUriString()

        val response = restTemplate.getForObject(uri, Map::class.java)?.let {
            return it["city"] as String
        }

        println("Response: $response")
        return "Latitude: $latitude, Longitude: $longitude"
    }

    fun getNearbyCities(latitude: Double, longitude: Double): List<String>? {
        val baseUrl = "http://getnearbycities.geobytes.com/GetNearbyCities"
        val uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("radius", 100)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .build()
            .toUri()

        val response = restTemplate.getForObject(uri, List::class.java).let {
            it?.let {
                val regex = Regex("^\\[\\s*[^,]*,\\s*([^,]*)")
                it.mapNotNull { element -> regex.find(element.toString())?.groupValues?.get(1) }
            }
        }

        if (response.isNullOrEmpty()) {
            return null
        }

        return response
    }

}