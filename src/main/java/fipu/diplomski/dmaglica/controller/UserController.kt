package fipu.diplomski.dmaglica.controller

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
    ) = userService.signup(email, username, password)

    @GetMapping(Paths.LOGIN)
    fun login(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String
    ) = userService.login(email, password)

    @PatchMapping(Paths.UPDATE_USER)
    fun updateUser() = userService.update()

    @DeleteMapping(Paths.DELETE_USER)
    fun deleteUser() = userService.delete()

}