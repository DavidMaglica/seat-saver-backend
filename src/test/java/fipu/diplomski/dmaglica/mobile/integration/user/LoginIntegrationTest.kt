package fipu.diplomski.dmaglica.mobile.integration.user

import fipu.diplomski.dmaglica.model.response.DataResponse
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

@Transactional
class LoginIntegrationTest : AbstractUserServiceIntegrationTest() {

    @Test
    fun `should login with correct password`() {
        val user = createCustomer()

        val signupResponse = userService.signup(user.email, user.username, user.password, isOwner = false)
        signupResponse.success `should be equal to` true

        val loginResponse: DataResponse<Int> = userService.login(user.email, user.password)

        loginResponse.success `should be equal to` true
        loginResponse.message `should be equal to` "User with email ${user.email} successfully logged in"
        loginResponse.data `should be equal to` signupResponse.data
    }

    @Test
    fun `should fail login with wrong password`() {
        val user = createCustomer()
        val wrongPassword = "wrongPassword"

        val signupResponse = userService.signup(user.email, user.username, user.password, isOwner = false)
        signupResponse.success `should be equal to` true

        val loginResponse: DataResponse<Int> = userService.login(user.email, wrongPassword)

        loginResponse.success `should be equal to` false
        loginResponse.message `should be equal to` "Incorrect password."
        loginResponse.data `should be equal to` null
    }

    @Test
    fun `should fail login when user not found`() {
        val email = "notfound@email.com"
        val loginResponse: DataResponse<Int> = userService.login(email, "irrelevantPassword")

        loginResponse.success `should be equal to` false
        loginResponse.message `should be equal to` "User with email $email does not exist."
        loginResponse.data `should be equal to` null
    }
}