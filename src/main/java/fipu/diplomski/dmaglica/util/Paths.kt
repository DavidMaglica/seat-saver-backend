package fipu.diplomski.dmaglica.util

class Paths {
    companion object {
        const val API_V1 = "/api/v1"

        const val USERS = "$API_V1/users"
        const val SIGNUP = "$USERS/signup"
        const val LOGIN = "$USERS/login"
        const val USER_BY_ID = "$USERS/{userId}"
        const val USERS_BY_IDS = "$USERS/by-ids"
        const val USER_NOTIFICATIONS = "$USER_BY_ID/notifications"
        const val USER_LOCATION = "$USER_BY_ID/location"
        const val UPDATE_EMAIL = "$USER_BY_ID/email"
        const val UPDATE_USERNAME = "$USER_BY_ID/username"
        const val UPDATE_PASSWORD = "$USER_BY_ID/password"
        const val UPDATE_LOCATION = "$USER_BY_ID/location"
        const val UPDATE_NOTIFICATIONS = "$USER_BY_ID/notifications"

        const val VENUES = "$API_V1/venues"
        const val VENUE_BY_ID = "$VENUES/{venueId}"
        const val VENUE_BY_OWNER = "$VENUES/owner/{ownerId}"
        const val VENUE_HEADER_IMAGE = "$VENUE_BY_ID/header-image"
        const val VENUE_IMAGES = "$VENUE_BY_ID/venue-images"
        const val MENU_IMAGES = "$VENUE_BY_ID/menu-images"
        const val VENUE_AVERAGE_RATING = "$VENUE_BY_ID/average-rating"
        const val ALL_VENUE_RATINGS = "$VENUE_BY_ID/ratings"
        const val OVERALL_RATING = "$VENUES/overall-rating/{ownerId}"
        const val RATINGS_COUNT = "$VENUES/ratings/count/{ownerId}"
        const val VENUE_UTILISATION_RATE = "$VENUES/utilisation-rate/{ownerId}"
        const val RATE_VENUE = "$VENUE_BY_ID/rate"
        const val VENUE_TYPE = "$VENUES/type/{typeId}"
        const val ALL_VENUE_TYPES = "$VENUES/types"

        const val RESERVATIONS = "$API_V1/reservations"
        const val RESERVATION_BY_ID = "$RESERVATIONS/{reservationId}"
        const val USER_RESERVATIONS = "$RESERVATIONS/user/{userId}"
        const val OWNER_RESERVATIONS = "$RESERVATIONS/owner/{ownerId}"
        const val RESERVATIONS_COUNT = "$RESERVATIONS/count/{ownerId}"

        const val GEOLOCATION = "$API_V1/geolocation"
        const val GET_NEARBY_CITIES = "$GEOLOCATION/nearby-cities"

        const val SUPPORT = "$API_V1/support"
        const val SEND_EMAIL = "$SUPPORT/send-email"
    }
}
