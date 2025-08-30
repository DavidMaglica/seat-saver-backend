package fipu.diplomski.dmaglica.mobile.integration.user

import fipu.diplomski.dmaglica.model.data.Role
import fipu.diplomski.dmaglica.repo.NotificationOptionsRepository
import fipu.diplomski.dmaglica.repo.ReservationRepository
import fipu.diplomski.dmaglica.repo.UserRepository
import fipu.diplomski.dmaglica.repo.entity.NotificationOptionsEntity
import fipu.diplomski.dmaglica.repo.entity.UserEntity
import fipu.diplomski.dmaglica.service.UserService
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractUserServiceIntegrationTest {

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var notificationOptionsRepository: NotificationOptionsRepository

    @Autowired
    protected lateinit var reservationRepository: ReservationRepository

    @Autowired
    protected lateinit var userService: UserService


    protected fun createCustomer(name: String = "Test user", email: String = "user@email.com"): UserEntity =
        UserEntity().apply {
            this.username = name
            this.email = email
            this.password = "password"
            this.lastKnownLatitude = 0.0
            this.lastKnownLongitude = 0.0
            this.roleId = Role.CUSTOMER.ordinal
        }

    protected fun createNotificationOptions(userId: Int) = NotificationOptionsEntity().apply {
        this.userId = userId
        this.emailNotificationsEnabled = true
        this.pushNotificationsEnabled = true
        this.locationServicesEnabled = false
    }

}