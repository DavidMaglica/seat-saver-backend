package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.model.User
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.RoleRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.utils.convertToModel
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val notificationOptionsRepository: NotificationOptionsRepository,
    private val roleRepository: RoleRepository,
) {

    fun create() {
    }

    fun getUser(email: String): User {
        val user = try {
            userRepository.findByEmail(email)
        } catch (e: Exception) {
            throw SQLException("Error while fetching user with email $email", e)
        } ?: throw UserNotFoundException("User with email $email not found")

        val userNotificationOptions = notificationOptionsRepository.findByUserId(user.id)
        val userRoles = roleRepository.findAllByUserId(user.id)

        return User(
            id = user.id,
            email = user.email,
            username = user.username,
            password = user.password,
            role = userRoles.convertToModel(),
            notificationOptions = userNotificationOptions.convertToModel(),
        )
    }

    fun update() {
    }

    fun delete() {
    }

}