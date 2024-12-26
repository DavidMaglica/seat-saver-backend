package fipu.diplomski.dmaglica.util

class Paths {
    companion object {
        const val USER = "/user"
        const val SIGNUP = "/signup"
        const val LOGIN = "/login"
        const val LOGOUT = "/logout"
        const val CREATE_USER = "/create-user"
        const val GET_USER = "/get-user"
        const val UPDATE_USER_EMAIL = "/update-user-email"
        const val UPDATE_USER_USERNAME = "/update-user-username"
        const val UPDATE_USER_PASSWORD = "/update-user-password"
        const val DELETE_USER = "/delete-user"

        const val VENUE = "/venue"
        const val CREATE_VENUE = "/create-venue"
        const val GET_VENUE = "/get-venue"
        const val UPDATE_VENUE = "/update-venue"
        const val DELETE_VENUE = "/delete-venue"

        const val RESERVATION = "/reservation"
        const val CREATE_RESERVATION = "/create-reservation"
        const val GET_RESERVATION = "/get-reservation"
        const val UPDATE_RESERVATION = "/update-reservation"
        const val DELETE_RESERVATION = "/delete-reservation"
    }
}