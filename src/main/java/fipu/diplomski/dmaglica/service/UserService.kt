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
            return BasicResponse(success = false, message = "User with email $email already exists")
        }

        dbActionWithTryCatch("Error while saving user with email $email") {
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

        dbActionWithTryCatch("Error while saving notification options for user with email $email") {
            notificationOptionsRepository.saveAndFlush(
                NotificationOptionsEntity().also {
                    it.userId = user.id
                    it.locationServicesTurnedOn = false
                    it.pushNotificationsTurnedOn = false
                    it.emailNotificationsTurnedOn = false
                }
            )
        }
        dbActionWithTryCatch("Error while saving role for user with email $email") {
            roleRepository.saveAndFlush(
                RoleEntity().also {
                    it.userId = user.id
                    it.role = Role.USER.name
                }
            )
        }

        return BasicResponse(success = true, message = "User with email $email successfully created")
    }

    fun login(email: String, password: String): BasicResponse {
        dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: return BasicResponse(success = false, message = "User with email $email does not exist")

        return BasicResponse(success = true, message = "User with email $email successfully logged in")
    }

    fun getNotificationOptions(email: String): NotificationOptions {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        val notificationOptions = notificationOptionsRepository.getByUserId(user.id)

        return NotificationOptions(
            pushNotificationsTurnedOn = notificationOptions.pushNotificationsTurnedOn,
            emailNotificationsTurnedOn = notificationOptions.emailNotificationsTurnedOn,
            locationServicesTurnedOn = notificationOptions.locationServicesTurnedOn,
        )
    }

    fun updateEmail(email: String, newEmail: String): BasicResponse {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        dbActionWithTryCatch("Error while updating email for user with email $email") {
            user.email = newEmail
            userRepository.saveAndFlush(user)
        }

        return BasicResponse(success = true, message = "Email for user with email $email successfully updated")
    }

    fun updateUsername(email: String, newUsername: String): BasicResponse {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        dbActionWithTryCatch("Error while updating username for user with email $email") {
            user.username = newUsername
            userRepository.saveAndFlush(user)
        }

        return BasicResponse(success = true, message = "Username for user with email $email successfully updated")
    }

    fun updatePassword(email: String, newPassword: String): BasicResponse {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        user.password = newPassword
        dbActionWithTryCatch("Error while updating password for user with email $email") {
            userRepository.saveAndFlush(user)
        }

        return BasicResponse(success = true, message = "Password for user with email $email successfully updated")
    }

    fun updateNotificationOptions(
        email: String,
        pushNotificationsTurnedOn: Boolean,
        emailNotificationsTurnedOn: Boolean,
        locationServicesTurnedOn: Boolean
    ): BasicResponse {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        val notificationOptions = notificationOptionsRepository.getByUserId(user.id)

        notificationOptions.pushNotificationsTurnedOn = pushNotificationsTurnedOn
        notificationOptions.emailNotificationsTurnedOn = emailNotificationsTurnedOn
        notificationOptions.locationServicesTurnedOn = locationServicesTurnedOn
        dbActionWithTryCatch("Error while updating notification options for user with email $email") {
            notificationOptionsRepository.saveAndFlush(notificationOptions)
        }

        return BasicResponse(
            success = true,
            message = "Notification options for user with email $email successfully updated"
        )
    }

    fun updateLocation(email: String, latitude: Double, longitude: Double): BasicResponse {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.getByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        user.lastKnownLatitude = latitude
        user.lastKnownLongitude = longitude
        dbActionWithTryCatch("Error while updating location for user with email $email") {
            userRepository.saveAndFlush(user)
        }

        return BasicResponse(success = true, message = "Location for user with email $email successfully updated")
    }

    fun delete(email: String): BasicResponse {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.findByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        dbActionWithTryCatch("Error while deleting user with email $email") {
            userRepository.deleteById(user.id)
        }

        return BasicResponse(success = true, message = "User with email $email successfully deleted")
    }

    fun getUser(email: String): User {
        val user = dbActionWithTryCatch("Error while fetching user with email $email") {
            userRepository.findByEmail(email)
        } ?: throw UserNotFoundException("User with email $email does not exist")

        val notificationOptions = notificationOptionsRepository.getByUserId(user.id).let {
            NotificationOptions(
                pushNotificationsTurnedOn = it.pushNotificationsTurnedOn,
                emailNotificationsTurnedOn = it.emailNotificationsTurnedOn,
                locationServicesTurnedOn = it.locationServicesTurnedOn,
            )
        }

        val role = roleRepository.getAllByUserId(user.id).map { Role.valueOf(it.role) }

        return User(
            id = user.id,
            username = user.username,
            password = user.password,
            email = user.email,
            notificationOptions = notificationOptions,
            role = role,
            lastKnownLatitude = user.lastKnownLatitude,
            lastKnownLongitude = user.lastKnownLongitude,
        )
    }

    private inline fun <reified T> dbActionWithTryCatch(errorMessage: String, block: () -> T): T = try {
        block()
    } catch (e: Exception) {
        throw SQLException(errorMessage, e)
    }

}
