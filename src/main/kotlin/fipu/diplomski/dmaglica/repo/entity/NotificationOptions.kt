package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "Notification_options")
class NotificationOptions {
    @Id
    var id: Long = 0

    var userId: Long = 0

    var pushNotificationsTurnedOn = false

    var emailNotificationsTurnedOn = false

    var locationServicesTurnedOn = false

    override fun toString(): String =
        "NotificationOptions(id=$id, userId=$userId, pushNotificationsTurnedOn=$pushNotificationsTurnedOn, emailNotificationsTurnedOn=$emailNotificationsTurnedOn, locationServicesTurnedOn=$locationServicesTurnedOn)"

}