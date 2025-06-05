package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.data.NotificationOptions
import fipu.diplomski.dmaglica.model.data.User
import fipu.diplomski.dmaglica.model.data.UserLocation
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.model.response.DataResponse
import fipu.diplomski.dmaglica.repo.entity.UserEntity
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
    ): DataResponse<UserEntity> = userService.signup(email, username, password)

    @GetMapping(Paths.LOGIN)
    fun login(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String
    ): DataResponse<UserEntity> = userService.login(email, password)

    @GetMapping(Paths.GET_NOTIFICATION_OPTIONS)
    fun getUserNotificationOptions(@RequestParam("userId") userId: Int): NotificationOptions? =
        userService.getNotificationOptions(userId)

    @GetMapping(Paths.GET_LOCATION)
    fun getUserLocation(@RequestParam("userId") userId: Int): UserLocation? = userService.getLocation(userId)

    @PatchMapping(Paths.UPDATE_EMAIL)
    fun updateUserEmail(
        @RequestParam("userId") userId: Int,
        @RequestParam("newEmail") newEmail: String
    ): BasicResponse = userService.updateEmail(userId, newEmail)

    @PatchMapping(Paths.UPDATE_USERNAME)
    fun updateUserUsername(
        @RequestParam("userId") userId: Int,
        @RequestParam("newUsername") newUsername: String
    ): BasicResponse = userService.updateUsername(userId, newUsername)

    @PatchMapping(Paths.UPDATE_PASSWORD)
    fun updateUserPassword(
        @RequestParam("userId") userId: Int,
        @RequestParam("newPassword") newPassword: String
    ): BasicResponse = userService.updatePassword(userId, newPassword)

    @PatchMapping(Paths.UPDATE_NOTIFICATION_OPTIONS)
    fun updateUserNotificationOptions(
        @RequestParam("userId") userId: Int,
        @RequestParam("pushNotificationsTurnedOn") pushNotificationsTurnedOn: Boolean,
        @RequestParam("emailNotificationsTurnedOn") emailNotificationsTurnedOn: Boolean,
        @RequestParam("locationServicesTurnedOn") locationServicesTurnedOn: Boolean
    ): BasicResponse = userService.updateNotificationOptions(
        userId,
        pushNotificationsTurnedOn,
        emailNotificationsTurnedOn,
        locationServicesTurnedOn
    )

    @PatchMapping(Paths.UPDATE_LOCATION)
    fun updateUserLocation(
        @RequestParam("userId") userId: Int,
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double
    ): BasicResponse = userService.updateLocation(userId, latitude, longitude)

    @DeleteMapping(Paths.DELETE_USER)
    fun deleteUser(@RequestParam("userId") userId: Int): BasicResponse = userService.delete(userId)

    @GetMapping(Paths.GET_USER)
    fun getUser(@RequestParam("userId") userId: Int): User? = userService.getUser(userId)

}
