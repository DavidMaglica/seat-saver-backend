package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.UserNotFoundException
import fipu.diplomski.dmaglica.model.*
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.util.dbActionWithTryCatch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val notificationOptionsRepository: NotificationOptionsRepository,
) {

    @Transactional
    fun signup(email: String, username: String, password: String): BasicResponse {
        userRepository.getByEmail(email)?.let {
            return BasicResponse(success = false, message = "User with email $email already exists")
        }

        val user = UserEntity().also {
            it.id
            it.email = email
            it.username = username
            it.password = password
            it.roleId = Role.USER.ordinal
        }

        val userNotificationOptions = NotificationOptionsEntity().also {
            it.userId = user.id
            it.locationServicesTurnedOn = false
            it.pushNotificationsTurnedOn = false
            it.emailNotificationsTurnedOn = false
        }

        dbActionWithTryCatch("Error while saving user with email $email") {
            userRepository.save(user)
            notificationOptionsRepository.save(userNotificationOptions)
        }

        return BasicResponse(success = true, message = "User with email $email successfully created")
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): BasicResponse {
        userRepository.getByEmail(email)
            ?: return BasicResponse(success = false, message = "User with email $email does not exist")

        return BasicResponse(success = true, message = "User with email $email successfully logged in")
    }

    @Transactional(readOnly = true)
    fun getNotificationOptions(email: String): NotificationOptions {
        val user = findUserIfExists(email)

        val notificationOptions = notificationOptionsRepository.getByUserId(user.id)

        return NotificationOptions(
            pushNotificationsTurnedOn = notificationOptions.pushNotificationsTurnedOn,
            emailNotificationsTurnedOn = notificationOptions.emailNotificationsTurnedOn,
            locationServicesTurnedOn = notificationOptions.locationServicesTurnedOn,
        )
    }

    @Transactional(readOnly = true)
    fun getLocation(email: String): UserLocation? {
        val user = findUserIfExists(email)

        if (user.lastKnownLatitude == null || user.lastKnownLongitude == null) {
            return null
        }

        return UserLocation(latitude = user.lastKnownLatitude!!, longitude = user.lastKnownLongitude!!)
    }

    @Transactional
    fun updateEmail(email: String, newEmail: String): BasicResponse {
        val user = findUserIfExists(email)

        user.email = newEmail
        dbActionWithTryCatch("Error while updating email for user with email $email") {
            userRepository.save(user)
        }

        return BasicResponse(success = true, message = "Email for user with email $email successfully updated")
    }

    @Transactional
    fun updateUsername(email: String, newUsername: String): BasicResponse {
        val user = findUserIfExists(email)

        user.username = newUsername
        dbActionWithTryCatch("Error while updating username for user with email $email") {
            userRepository.save(user)
        }

        return BasicResponse(success = true, message = "Username for user with email $email successfully updated")
    }

    @Transactional
    fun updatePassword(email: String, newPassword: String): BasicResponse {
        val user = findUserIfExists(email)

        user.password = newPassword
        dbActionWithTryCatch("Error while updating password for user with email $email") {
            userRepository.save(user)
        }

        return BasicResponse(success = true, message = "Password for user with email $email successfully updated")
    }

    @Transactional
    fun updateNotificationOptions(
        email: String,
        pushNotificationsTurnedOn: Boolean,
        emailNotificationsTurnedOn: Boolean,
        locationServicesTurnedOn: Boolean
    ): BasicResponse {
        val user = findUserIfExists(email)

        val notificationOptions = notificationOptionsRepository.getByUserId(user.id)

        notificationOptions.pushNotificationsTurnedOn = pushNotificationsTurnedOn
        notificationOptions.emailNotificationsTurnedOn = emailNotificationsTurnedOn
        notificationOptions.locationServicesTurnedOn = locationServicesTurnedOn
        dbActionWithTryCatch("Error while updating notification options for user with email $email") {
            notificationOptionsRepository.save(notificationOptions)
        }

        return BasicResponse(
            success = true,
            message = "Notification options for user with email $email successfully updated"
        )
    }

    @Transactional
    fun updateLocation(email: String, latitude: Double, longitude: Double): BasicResponse {
        val user = findUserIfExists(email)

        user.lastKnownLatitude = latitude
        user.lastKnownLongitude = longitude
        dbActionWithTryCatch("Error while updating location for user with email $email") {
            userRepository.save(user)
        }

        return BasicResponse(success = true, message = "Location for user with email $email successfully updated")
    }

    @Transactional
    fun delete(email: String): BasicResponse {
        val user = findUserIfExists(email)

        dbActionWithTryCatch("Error while deleting user with email $email") {
            userRepository.deleteById(user.id)
        }

        return BasicResponse(success = true, message = "User with email $email successfully deleted")
    }

    @Transactional(readOnly = true)
    fun getUser(email: String): User {
        val user = findUserIfExists(email)

        val notificationOptions = notificationOptionsRepository.getByUserId(user.id).let {
            NotificationOptions(
                pushNotificationsTurnedOn = it.pushNotificationsTurnedOn,
                emailNotificationsTurnedOn = it.emailNotificationsTurnedOn,
                locationServicesTurnedOn = it.locationServicesTurnedOn,
            )
        }

        return User(
            id = user.id,
            username = user.username,
            password = user.password,
            email = user.email,
            notificationOptions = notificationOptions,
            role = Role.entries[user.roleId],
            lastKnownLatitude = user.lastKnownLatitude,
            lastKnownLongitude = user.lastKnownLongitude,
        )
    }

    private fun findUserIfExists(email: String) =
        userRepository.findByEmail(email) ?: throw UserNotFoundException("User with email $email does not exist")
}
