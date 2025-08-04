package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.model.data.NotificationOptions
import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.model.data.User
import fipu.diplomski.dmaglica.model.data.UserLocation
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.model.response.DataResponse
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val userRepository: UserRepository,
    private val notificationOptionsRepository: NotificationOptionsRepository,
) {

    companion object {
        private val logger = KotlinLogging.logger(UserService::class.java.name)
        private val passwordEncoder = BCryptPasswordEncoder()
    }

    @Transactional
    fun signup(email: String, username: String, password: String, isOwner: Boolean): DataResponse<Int> {
        userRepository.findByEmail(email)?.let {
            return DataResponse(false, "User with email $email already exists")
        }

        val hashedPassword = passwordEncoder.encode(password)

        val role = if (isOwner) Role.OWNER else Role.USER

        val user = UserEntity().also {
            it.email = email
            it.username = username
            it.password = hashedPassword
            it.roleId = role.ordinal
        }

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            logger.error { "Error while saving user with email $email. Error: ${e.message}" }
            return DataResponse(false, "Error while creating user. Please try again later.", null)
        }

        try {
            val userNotificationOptions = NotificationOptionsEntity().also {
                it.userId = user.id
                it.locationServicesEnabled = false
                it.pushNotificationsEnabled = false
                it.emailNotificationsEnabled = false
            }
            notificationOptionsRepository.save(userNotificationOptions)
        } catch (e: Exception) {
            logger.error { "Error while saving notification options for user with email $email. Error: ${e.message}" }
            return DataResponse(
                false,
                "Error while creating user notification options. Please try again later.",
                null
            )
        }

        return DataResponse(true, "User with email $email successfully created", user.id)
    }

    @Transactional(readOnly = true)
    fun login(email: String, password: String): DataResponse<Int> {
        val user = userRepository.findByEmail(email)
            ?: return DataResponse(false, "User with email $email does not exist.", null)

        if (!passwordEncoder.matches(password, user.password)) {
            return DataResponse(false, "Incorrect password.", null)
        }

        return DataResponse(true, "User with email $email successfully logged in", user.id)
    }

    @Transactional(readOnly = true)
    fun getNotificationOptions(userId: Int): NotificationOptions? {
        val user = userRepository.findById(userId).getOrElse { return null }

        val notificationOptions = notificationOptionsRepository.findByUserId(user.id)

        return NotificationOptions(
            pushNotificationsTurnedOn = notificationOptions.pushNotificationsEnabled,
            emailNotificationsTurnedOn = notificationOptions.emailNotificationsEnabled,
            locationServicesTurnedOn = notificationOptions.locationServicesEnabled,
        )
    }

    @Transactional(readOnly = true)
    fun getLocation(userId: Int): UserLocation? {
        val user = userRepository.findById(userId).getOrElse { return null }

        if (user.lastKnownLatitude == null || user.lastKnownLongitude == null) {
            return null
        }

        return UserLocation(latitude = user.lastKnownLatitude!!, longitude = user.lastKnownLongitude!!)
    }

    @Transactional
    fun updateEmail(userId: Int, newEmail: String): BasicResponse {
        if (newEmail.isBlank()) {
            return BasicResponse(false, "Email cannot be empty.")
        }

        userRepository.findByEmail(newEmail)?.let {
            return BasicResponse(false, "User with email $newEmail already exists.")
        }

        val user = userRepository.findById(userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }.apply {
            email = newEmail
        }

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            logger.error { "Error while updating email for user with email ${user.email}. Error: ${e.message}" }
            return BasicResponse(false, "Error while updating email. Please try again later.")
        }

        return BasicResponse(true, "Email updated to $newEmail successfully.")
    }

    @Transactional
    fun updateUsername(userId: Int, newUsername: String): BasicResponse {
        if (newUsername.isBlank()) {
            return BasicResponse(false, "Username cannot be empty.")
        }

        val user = userRepository.findById(userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }.apply {
            username = newUsername
        }

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            logger.error { "Error while updating username for user with email ${user.email}. Error: ${e.message}" }
            return BasicResponse(false, "Error while updating username. Please try again later.")
        }

        return BasicResponse(true, "Username successfully updated.")
    }

    @Transactional
    fun updatePassword(userId: Int, newPassword: String): BasicResponse {
        if (newPassword.isBlank()) {
            return BasicResponse(false, "Password cannot be empty.")
        }

        val hashedPassword = passwordEncoder.encode(newPassword)

        val user = userRepository.findById(userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }.apply {
            password = hashedPassword
        }

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            logger.error { "Error while updating password for user with email ${user.email}. Error: ${e.message}" }
            return BasicResponse(false, "Error while updating password. Please try again later.")
        }

        return BasicResponse(true, "Password successfully updated.")
    }

    @Transactional
    fun updateNotificationOptions(
        userId: Int,
        pushNotificationsTurnedOn: Boolean,
        emailNotificationsTurnedOn: Boolean,
        locationServicesTurnedOn: Boolean
    ): BasicResponse {
        val user = userRepository.findById(userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }

        val notificationOptions = notificationOptionsRepository.findByUserId(user.id).apply {
            pushNotificationsEnabled = pushNotificationsTurnedOn
            emailNotificationsEnabled = emailNotificationsTurnedOn
            locationServicesEnabled = locationServicesTurnedOn
        }

        try {
            notificationOptionsRepository.save(notificationOptions)
        } catch (e: Exception) {
            logger.error { "Error while updating notification options for user with email ${user.email}. Error: ${e.message}" }
            return BasicResponse(false, "Error while updating notification options. Please try again later.")
        }

        return BasicResponse(
            true,
            "Notification options successfully updated."
        )
    }

    @Transactional
    fun updateLocation(userId: Int, latitude: Double, longitude: Double): BasicResponse {
        val user: UserEntity = userRepository.findById(userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }.apply {
            lastKnownLatitude = latitude
            lastKnownLongitude = longitude
        }

        try {
            userRepository.save(user)
        } catch (e: Exception) {
            logger.error { "Error while updating location for user with email ${user.email}. Error: ${e.message}" }
            return BasicResponse(false, "Error while updating location. Please try again later.")
        }

        return BasicResponse(true, "Location successfully updated.")
    }

    @Transactional
    fun delete(userId: Int): BasicResponse {
        val user: UserEntity = userRepository.findById(userId).getOrElse {
            return BasicResponse(false, "User not found.")
        }

        try {
            userRepository.deleteById(user.id)
        } catch (e: Exception) {
            logger.error { "Error while deleting user with id $userId. Error: ${e.message}" }
            return BasicResponse(false, "Error while deleting user. Please try again later.")
        }

        return BasicResponse(true, "User successfully deleted.")
    }

    @Transactional(readOnly = true)
    fun getUser(userId: Int): User? {
        val user: UserEntity = userRepository.findById(userId).getOrElse { return null }

        val notificationOptions = notificationOptionsRepository.findByUserId(user.id).let {
            NotificationOptions(
                pushNotificationsTurnedOn = it.pushNotificationsEnabled,
                emailNotificationsTurnedOn = it.emailNotificationsEnabled,
                locationServicesTurnedOn = it.locationServicesEnabled,
            )
        }

        return User(
            id = user.id,
            username = user.username,
            email = user.email,
            notificationOptions = notificationOptions,
            role = Role.entries[user.roleId],
            lastKnownLatitude = user.lastKnownLatitude,
            lastKnownLongitude = user.lastKnownLongitude,
        )
    }
}
