package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "notification_options")
class NotificationOptionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    var userId: Int = 0

    var pushNotificationsTurnedOn = false

    var emailNotificationsTurnedOn = false

    var locationServicesTurnedOn = false

}
