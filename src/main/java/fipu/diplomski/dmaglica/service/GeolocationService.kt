package fipu.diplomski.dmaglica.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit


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

    /**
     * In-memory cache for nearby cities lookups to reduce calls to the external
     * GeoDB Cities API and avoid hitting rate limits.
     *
     * - Keys are pairs of latitude/longitude coordinates.
     * - Values are lists of nearby city names returned by the API.
     * - Cache entries expire 1 hour after being written.
     * - Maximum cache size is limited to 1000 entries to prevent unbounded memory usage.
     */
    private val cache = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build<Pair<Double, Double>, List<String>>()


    /**
     * Performs a reverse geocoding lookup to resolve a human-readable city name
     * for the given latitude and longitude.
     *
     * Uses the [BigDataCloud Reverse Geocode API](https://www.bigdatacloud.com/geocoding-apis/reverse-geocode-to-city-api)
     * via a simple REST call.
     *
     * @param latitude The latitude of the location to resolve.
     * @param longitude The longitude of the location to resolve.
     * @return The city name for the given coordinates, or `"Zagreb"` if the lookup fails
     *         or the API does not return a valid city field.
     *
     * Error handling:
     * - Logs an error if the request fails or the API response is invalid.
     * - Returns the default `"Zagreb"` as a fallback to ensure a non-null result.
     */
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

    fun getNearbyCities(latitude: Double, longitude: Double): MutableList<String> {
        val limit = 10
        val radius = 100
        val minPopulation = 1000
        val key = latitude to longitude

        return cache.get(key) {
            try {
                val request = HttpRequest.newBuilder()
                    .uri(
                        URI.create(
                            "https://wft-geo-db.p.rapidapi.com/v1/geo/locations/$latitude%2B$longitude/nearbyCities" +
                                    "?radius=$radius&limit=$limit&minPopulation=$minPopulation"
                        )
                    )
                    .header("x-rapidapi-key", apiKey)
                    .header("x-rapidapi-host", "wft-geo-db.p.rapidapi.com")
                    .GET()
                    .build()

                val response: HttpResponse<String> =
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())

                val responseBody = objectMapper.readTree(response.body())
                val dataNode = responseBody["data"]

                if (dataNode == null || !dataNode.isArray) {
                    logger.error { "Nearby cities response does not contain expected 'data' array: $responseBody" }
                    emptyList()
                } else {
                    dataNode.mapNotNull { it["city"]?.asText() }
                }
            } catch (e: Exception) {
                logger.error(e) { "Failed to fetch nearby cities. Error: ${e.message}" }
                emptyList()
            }
        }.toMutableList()
    }
}
