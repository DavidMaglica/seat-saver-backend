package fipu.diplomski.dmaglica.util

class Paths {
    companion object {
        const val USER = "/user"
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val GET_USER = "/get-user"
        const val GET_USER_NOTIFICATION_OPTIONS = "/get-user-notification-options"
        const val GET_USER_LOCATION = "/get-user-location"
        const val UPDATE_USER_EMAIL = "/update-user-email"
        const val UPDATE_USER_USERNAME = "/update-user-username"
        const val UPDATE_USER_PASSWORD = "/update-user-password"
        const val UPDATE_USER_LOCATION = "/update-user-location"
        const val UPDATE_USER_NOTIFICATION_OPTIONS = "/update-user-notification-options"
        const val DELETE_USER = "/delete-user"

        const val VENUE = "/venue"
        const val CREATE_VENUE = "/create"
        const val UPLOAD_VENUE_IMAGE = "/upload-image"
        const val GET_VENUE = "/get"
        const val GET_VENUE_TYPE = "/get-type"
        const val GET_VENUE_MENU = "/get-menu"
        const val UPLOAD_MENU_IMAGE = "/upload-menu-image"
        const val UPDATE_VENUE = "/update"
        const val RATE_VENUE = "/rate"
        const val DELETE_VENUE = "/delete"

        const val RESERVATION = "/reservation"
        const val CREATE_RESERVATION = "/create"
        const val GET_RESERVATION = "/get"
        const val UPDATE_RESERVATION = "/update"
        const val DELETE_RESERVATION = "/delete"

        const val GEOLOCATION = "/geolocation"
        const val FETCH_GEOLOCATION = "/fetch-geolocation"
        const val GET_GEOLOCATION = "/get-geolocation"
        const val GET_NEARBY_CITIES = "/get-nearby-cities"
    }
}