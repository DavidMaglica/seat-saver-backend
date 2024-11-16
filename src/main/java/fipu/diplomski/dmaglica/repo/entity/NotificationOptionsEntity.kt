package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "notification_options")
class NotificationOptionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var userId: Long = 0

    var pushNotificationsTurnedOn = false

    var emailNotificationsTurnedOn = false

    var locationServicesTurnedOn = false

    override fun toString(): String =
        "NotificationOptions(id=$id, userId=$userId, pushNotificationsTurnedOn=$pushNotificationsTurnedOn, emailNotificationsTurnedOn=$emailNotificationsTurnedOn, locationServicesTurnedOn=$locationServicesTurnedOn)"

}