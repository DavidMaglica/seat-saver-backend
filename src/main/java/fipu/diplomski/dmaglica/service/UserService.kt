package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.model.BasicResponse
import fipu.diplomski.dmaglica.model.NotificationOptions
import fipu.diplomski.dmaglica.model.Role
import fipu.diplomski.dmaglica.model.User
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.RoleRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.RoleEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class UserService(
    private val userRepository: UserRepository,
    private val notificationOptionsRepository: NotificationOptionsRepository,
    private val roleRepository: RoleRepository,
) {

    fun signup(email: String, username: String, password: String): BasicResponse {
        userRepository.getByEmail(email)?.let {
            return BasicResponse(false, "User with email $email already exists")
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
        val user = userRepository.getByEmail(email) ?: throw UserNotFoundException("User was not created successfully")

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

        return BasicResponse(true, "User with email $email successfully created")
    }

    fun login(email: String, password: String): BasicResponse {
        withTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: return BasicResponse(false, "User with email $email does not exist")

        return BasicResponse(true, "User with email $email successfully logged in")
    }

    fun updateEmail(email: String, newEmail: String): BasicResponse {
        val user = withTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        withTryCatch("Error while updating email for user with email $email") {
            user.email = newEmail
            userRepository.saveAndFlush(user)
        }

        return BasicResponse(true, "Email for user with email $email successfully updated")
    }

    fun updateUsername(email: String, newUsername: String): BasicResponse {
        val user = withTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        withTryCatch("Error while updating username for user with email $email") {
            user.username = newUsername
            userRepository.saveAndFlush(user)
        }

        return BasicResponse(true, "Username for user with email $email successfully updated")
    }

    fun updatePassword(email: String, newPassword: String): BasicResponse {
        val user = withTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        user.password = newPassword
        withTryCatch("Error while updating password for user with email $email") {
            userRepository.saveAndFlush(user)
        }

        return BasicResponse(true, "Password for user with email $email successfully updated")
    }

    fun delete(email: String): BasicResponse {
        val user = withTryCatch("Error while fetching user with email $email") {
            userRepository.findByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        withTryCatch("Error while deleting user with email $email") {
            userRepository.deleteById(user.id)
        }

        return BasicResponse(true, "User with email $email successfully deleted")
    }

    fun getUser(email: String): User {
        val user = withTryCatch("Error while fetching user with email $email") {
            userRepository.findByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        val notificationOptions = notificationOptionsRepository.findByUserId(user.id).let {
            NotificationOptions(
                it.locationServicesTurnedOn,
                it.pushNotificationsTurnedOn,
                it.emailNotificationsTurnedOn,
            )
        }

        val role = roleRepository.findAllByUserId(user.id).map { Role.valueOf(it.role) }

        return User(
            user.id,
            user.username,
            user.password,
            user.email,
            notificationOptions,
            role,
            user.lastKnownLatitude,
            user.lastKnownLongitude,
        )
    }

    private inline fun <reified T> withTryCatch(errorMessage: String, block: () -> T): T = try {
        block()
    } catch (e: Exception) {
        throw SQLException(errorMessage, e)
    }

}
