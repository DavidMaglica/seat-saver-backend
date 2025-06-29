package fipu.diplomski.dmaglica.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse


@Service
class GeolocationService(
    private val restTemplate: RestTemplate,
    @Value("\${rapidapi.key}")
    private val apiKey: String
) {

    companion object {
        private val objectMapper = ObjectMapper()
        private val logger = KotlinLogging.logger(GeolocationService::class.java.name)
    }


    fun getGeolocation(latitude: Double, longitude: Double): String {
        val defaultLocation = "Zagreb"
        val baseUrl = "https://api-bdc.net/data/reverse-geocode-client"
        val uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("localityLanguage", "en")
            .build()
            .toUriString()

        try {
            restTemplate.getForObject(uri, Map::class.java)?.let {
                return it["city"] as String
            }
        } catch (e: RuntimeException) {
            logger.error(e) { "Failed to fetch geolocation. Error: ${e.message}" }
            return defaultLocation
        }

        return defaultLocation
    }

    fun getNearbyCities(latitude: Double, longitude: Double): MutableList<String>? {
        val limit = 10
        val radius = 100

        val request: HttpRequest? = HttpRequest.newBuilder()
            .uri(URI.create("https://wft-geo-db.p.rapidapi.com/v1/geo/locations/$latitude%2B$longitude/nearbyCities?radius=$radius&limit=$limit"))
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build()
        val response: HttpResponse<String?> = try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            logger.error { "Failed to fetch geolocation. Error: ${e.message}" }
            return null
        }

        val responseBody = objectMapper.readTree(response.body())
        val cities = responseBody["data"].mapNotNull { it["city"]?.asText() }

        return cities.toMutableList()
    }
}
