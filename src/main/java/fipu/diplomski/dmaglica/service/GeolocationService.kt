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
import java.util.concurrent.ConcurrentHashMap


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

    private val cache = ConcurrentHashMap<Pair<Double, Double>, List<String>>() // Needed to avoid rate limits


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
        val minPopulation = 1000
        val key = latitude to longitude
        cache[key]?.let { return it.toMutableList() }

        val request: HttpRequest? = HttpRequest.newBuilder()
            .uri(URI.create("https://wft-geo-db.p.rapidapi.com/v1/geo/locations/$latitude%2B$longitude/nearbyCities?radius=$radius&limit=$limit&minPopulation=$minPopulation"))
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build()
        val response: HttpResponse<String?> = try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: Exception) {
            logger.error { "Failed to fetch geolocation. Error: ${e.message}" }
            return mutableListOf()
        }

        val responseBody = objectMapper.readTree(response.body())
        val dataNode = responseBody["data"]
        if (dataNode == null || !dataNode.isArray) {
            logger.error { "Nearby cities response does not contain expected 'data' array: $responseBody" }
            return mutableListOf()
        }

        val cities = dataNode.mapNotNull { it["city"]?.asText() }
        cache[key] = cities
        return cities.toMutableList()
    }
}
