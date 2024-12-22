package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.BasicResponse
import fipu.diplomski.dmaglica.model.User
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

    @PatchMapping(Paths.UPDATE_USER_EMAIL)
    fun updateUserEmail(
        @RequestParam("email") email: String,
        @RequestParam("newEmail") newEmail: String
    ) = userService.updateEmail(email, newEmail)

    @PatchMapping(Paths.UPDATE_USER_USERNAME)
    fun updateUserUsername(
        @RequestParam("email") email: String,
        @RequestParam("newUsername") newUsername: String
    ) = userService.updateUsername(email, newUsername)

    @PatchMapping(Paths.UPDATE_USER_PASSWORD)
    fun updateUserPassword(
        @RequestParam("email") email: String,
        @RequestParam("newPassword") newPassword: String
    ) = userService.updatePassword(email, newPassword)

    @DeleteMapping(Paths.DELETE_USER)
    fun deleteUser(@RequestParam("email") email: String) = userService.delete(email)

    @GetMapping(Paths.GET_USER)
    fun getUser(@RequestParam email: String): User = userService.getUser(email)

}