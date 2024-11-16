package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.UserAlreadyExistsException
import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.model.Role
import fipu.diplomski.dmaglica.model.User
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.RoleRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.RoleEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.repo.utils.convertToModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val notificationOptionsRepository: NotificationOptionsRepository,
    private val roleRepository: RoleRepository,
) {

    fun signup(email: String, username: String, password: String) {
        userRepository.findByEmail(email)?.let {
            throw UserAlreadyExistsException("User with email $email already exists")
        }

        withTryCatch("Error while saving user with email $email") {
            userRepository.saveAndFlush(
                UserEntity().also {
                    it.id
                    it.email = email
                    it.username = username
                    it.password = password
                }
            )
        }
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User was not created successfully")

        withTryCatch("Error while saving notification options for user with email $email") {
            notificationOptionsRepository.saveAndFlush(
                NotificationOptionsEntity().also {
                    it.userId = user.id
                    it.locationServicesTurnedOn = false
                    it.pushNotificationsTurnedOn = false
                    it.emailNotificationsTurnedOn = false
                }
            )
        }
        withTryCatch("Error while saving role for user with email $email") {
            roleRepository.saveAndFlush(
                RoleEntity().also {
                    it.userId = user.id
                    it.role = Role.USER.name
                }
            )
        }

        flushRepositories(notificationOptionsRepository, roleRepository)
    }

    fun login(email: String, password: String): User {
        val user = withTryCatch("Error while fetching user with email $email") {
            userRepository.findByEmail(email)
        } ?: throw UserNotFoundException("User with email $email not found")

        return User(
            id = user.id,
            email = user.email,
            username = user.username,
            password = user.password,
            role = roleRepository.findAllByUserId(user.id).convertToModel(),
            notificationOptions = notificationOptionsRepository.findByUserId(user.id).convertToModel(),
            lastKnownLatitude = user.lastKnownLatitude,
            lastKnownLongitude = user.lastKnownLongitude,
        )
    }

    fun update() {
    }

    fun delete() {
    }

    private inline fun <reified T> withTryCatch(errorMessage: String, block: () -> T): T = try {
        block()
    } catch (e: Exception) {
        throw SQLException(errorMessage, e)
    }

    private fun flushRepositories(vararg repositories: JpaRepository<*, *>) = repositories.forEach { it.flush() }

}
