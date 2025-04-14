package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.data.NotificationOptions
import fipu.diplomski.dmaglica.model.data.User
import fipu.diplomski.dmaglica.model.data.UserLocation
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.service.UserService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Paths.USER)
class UserController(private val userService: UserService) {

    @PostMapping(Paths.SIGNUP)
    fun signup(
        @RequestParam("email") email: String,
        @RequestParam("username") username: String,
        @RequestParam("password") password: String
    ): BasicResponse = userService.signup(email, username, password)

    @GetMapping(Paths.LOGIN)
    fun login(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String
    ): BasicResponse = userService.login(email, password)

    @GetMapping(Paths.GET_USER_NOTIFICATION_OPTIONS)
    fun getUserNotificationOptions(@RequestParam("email") email: String): NotificationOptions =
        userService.getNotificationOptions(email)

    @GetMapping(Paths.GET_USER_LOCATION)
    fun getUserLocation(@RequestParam("email") email: String): UserLocation? = userService.getLocation(email)

    @PatchMapping(Paths.UPDATE_USER_EMAIL)
    fun updateUserEmail(
        @RequestParam("email") email: String,
        @RequestParam("newEmail") newEmail: String
    ): BasicResponse = userService.updateEmail(email, newEmail)

    @PatchMapping(Paths.UPDATE_USER_USERNAME)
    fun updateUserUsername(
        @RequestParam("email") email: String,
        @RequestParam("newUsername") newUsername: String
    ): BasicResponse = userService.updateUsername(email, newUsername)

    @PatchMapping(Paths.UPDATE_USER_PASSWORD)
    fun updateUserPassword(
        @RequestParam("email") email: String,
        @RequestParam("newPassword") newPassword: String
    ): BasicResponse = userService.updatePassword(email, newPassword)

    @PatchMapping(Paths.UPDATE_USER_NOTIFICATION_OPTIONS)
    fun updateUserNotificationOptions(
        @RequestParam("email") email: String,
        @RequestParam("pushNotificationsTurnedOn") pushNotificationsTurnedOn: Boolean,
        @RequestParam("emailNotificationsTurnedOn") emailNotificationsTurnedOn: Boolean,
        @RequestParam("locationServicesTurnedOn") locationServicesTurnedOn: Boolean
    ): BasicResponse = userService.updateNotificationOptions(
        email,
        pushNotificationsTurnedOn,
        emailNotificationsTurnedOn,
        locationServicesTurnedOn
    )

    @PatchMapping(Paths.UPDATE_USER_LOCATION)
    fun updateUserLocation(
        @RequestParam("email") email: String,
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ): BasicResponse = userService.updateLocation(email, latitude, longitude)

    @DeleteMapping(Paths.DELETE_USER)
    fun deleteUser(@RequestParam("email") email: String): BasicResponse = userService.delete(email)

    @GetMapping(Paths.GET_USER)
    fun getUser(@RequestParam("email") email: String): User = userService.getUser(email)

}
