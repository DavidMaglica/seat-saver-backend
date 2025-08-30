package fipu.diplomski.dmaglica.mobile.integration.user

import fipu.diplomski.dmaglica.model.response.BasicResponse
import jakarta.transaction.Transactional
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

@Transactional
class DeleteIntegrationTest : AbstractUserServiceIntegrationTest() {

    @Test
    fun `should delete user successfully`() {
        val user = createCustomer()
        userRepository.saveAndFlush(user)

        userRepository.findById(user.id).isPresent `should be equal to` true

        val response: BasicResponse = userService.delete(user.id)

        response.success `should be equal to` true
        response.message `should be equal to` "User successfully deleted."
    }

    @Test
    fun `should fail delete when user not found`() {
        val response: BasicResponse = userService.delete(999)

        response.success `should be equal to` false
        response.message `should be equal to` "User not found."
    }
}