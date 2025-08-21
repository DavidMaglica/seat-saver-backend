package fipu.diplomski.dmaglica.model.data

data class NotificationOptions(
    val isPushNotificationsEnabled: Boolean,
    val isEmailNotificationsEnabled: Boolean,
    val isLocationServicesEnabled: Boolean,
)
