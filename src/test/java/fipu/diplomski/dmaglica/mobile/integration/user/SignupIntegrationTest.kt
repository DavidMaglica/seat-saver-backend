package fipu.diplomski.dmaglica.mobile.integration.user

import fipu.diplomski.dmaglica.model.data.Role
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be`
import org.junit.jupiter.api.Test

@Transactional
class SignupIntegrationTest : AbstractUserServiceIntegrationTest() {

    @Test
    fun `should create user and notification options successfully and login with created user`() {
        val user = createCustomer()

        val signupResponse = userService.signup(user.email, user.username, user.password, isOwner = false)

        signupResponse.success `should be equal to` true
        signupResponse.message `should be equal to` "User with email ${user.email} successfully created"
        signupResponse.data `should not be` null

        val createdUser = userRepository.findById(signupResponse.data!!).get()
        createdUser.username `should be equal to` user.username
        createdUser.email `should be equal to` user.email
        createdUser.roleId `should be equal to` Role.CUSTOMER.ordinal

        val notificationOptions = notificationOptionsRepository.findByUserId(createdUser.id)
        notificationOptions.emailNotificationsEnabled `should be equal to` false
        notificationOptions.pushNotificationsEnabled `should be equal to` false
        notificationOptions.locationServicesEnabled `should be equal to` false

        val loginResponse = userService.login(user.email, user.password)

        loginResponse.success `should be equal to` true
        loginResponse.data `should be equal to` createdUser.id
    }

    @Test
    fun `should fail signup when email already exists`() {
        val existingUser = createCustomer()
        userRepository.saveAndFlush(existingUser)

        val response = userService.signup(existingUser.email, "anotherUser", "password123", isOwner = false)

        response.success `should be equal to` false
        response.message `should be equal to` "User with email ${existingUser.email} already exists"
        response.data `should be equal to` null
    }
}