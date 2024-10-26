package fipu.diplomski.dmaglica.model

data class NotificationOptions(
    val pushNotificationsTurnedOn: Boolean,
    val emailNotificationsTurnedOn: Boolean,
    val locationServicesTurnedOn: Boolean,
)
