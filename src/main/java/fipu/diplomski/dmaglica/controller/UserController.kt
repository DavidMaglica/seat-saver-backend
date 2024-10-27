package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.User
import fipu.diplomski.dmaglica.service.UserService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Paths.USER)
class UserController(private val userService: UserService) {

    @PostMapping(Paths.CREATE_USER)
    fun createUser() = userService.create()

    @GetMapping(Paths.GET_USER)
    fun getUser(@RequestParam("email") email: String): User = userService.getUser(email)

    @PatchMapping(Paths.UPDATE_USER)
    fun updateUser() = userService.update()

    @DeleteMapping(Paths.DELETE_USER)
    fun deleteUser() = userService.delete()

}