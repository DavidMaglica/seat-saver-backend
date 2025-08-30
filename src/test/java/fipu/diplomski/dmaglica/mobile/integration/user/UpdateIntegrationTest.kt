package fipu.diplomski.dmaglica.mobile.integration.user

import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be`
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Transactional
class UpdateIntegrationTest : AbstractUserServiceIntegrationTest() {

    private lateinit var user: UserEntity

    @BeforeEach
    fun setupUser() {
        user = createCustomer()
        userRepository.saveAndFlush(user)
        notificationOptionsRepository.saveAndFlush(createNotificationOptions(user.id))
    }

    @Test
    fun `should update email successfully and not allow login with old email`() {
        val oldEmail = user.email
        val newEmail = "newemail@test.com"
        val response: BasicResponse = userService.updateEmail(user.id, newEmail)

        response.success `should be` true
        response.message `should be equal to` "Email successfully updated to $newEmail."

        val updatedUser = userRepository.findById(user.id).get()
        updatedUser.email `should be equal to` newEmail

        val loginResponse = userService.login(oldEmail, user.password)

        loginResponse.success `should be` false
        loginResponse.message `should be equal to` "User with email $oldEmail does not exist."
    }

    @Test
    fun `should fail when updating to duplicate email`() {
        val anotherUser = createCustomer(email = "duplicate@test.com")
        userRepository.saveAndFlush(anotherUser)

        val response: BasicResponse = userService.updateEmail(user.id, "duplicate@test.com")

        response.success `should be` false
        response.message `should be equal to` "User with email duplicate@test.com already exists."
    }

    @Test
    fun `should update username successfully`() {
        val newUsername = "NewUserName"
        val response: BasicResponse = userService.updateUsername(user.id, newUsername)

        response.success `should be` true
        response.message `should be equal to` "Username successfully updated to $newUsername."

        val updatedUser = userRepository.findById(user.id).get()
        updatedUser.username `should be equal to` newUsername
    }

    @Test
    fun `should update password successfully and allow login with new password`() {
        val oldPasswordHash = user.password

        val newPassword = "newpassword123"
        val response: BasicResponse = userService.updatePassword(user.id, newPassword)

        response.success `should be` true
        response.message `should be equal to` "Password successfully updated."

        val updatedUser = userRepository.findById(user.id).get()
        updatedUser.password `should not be equal to` oldPasswordHash

        val loginResponse = userService.login(user.email, newPassword)
        loginResponse.success `should be` true
        loginResponse.message `should be equal to` "User with email ${user.email} successfully logged in"
        loginResponse.data `should be equal to` user.id
    }

    @Test
    fun `should update location successfully`() {
        val lat = 45.0
        val lon = 15.0
        val response: BasicResponse = userService.updateLocation(user.id, lat, lon)

        response.success `should be` true
        response.message `should be equal to` "Location successfully updated."

        val updatedUser = userRepository.findById(user.id).get()
        updatedUser.lastKnownLatitude `should be equal to` lat
        updatedUser.lastKnownLongitude `should be equal to` lon
    }

    @Test
    fun `should update notification options successfully`() {
        val response: BasicResponse = userService.updateNotificationOptions(
            user.id, isPushNotificationsEnabled = true,
            isEmailNotificationsEnabled = false,
            isLocationServicesEnabled = true
        )

        response.success `should be` true
        response.message `should be equal to` "Notification options successfully updated."

        val updatedOptions = notificationOptionsRepository.findByUserId(user.id)
        updatedOptions.pushNotificationsEnabled `should be` true
        updatedOptions.emailNotificationsEnabled `should be` false
        updatedOptions.locationServicesEnabled `should be` true
    }
}