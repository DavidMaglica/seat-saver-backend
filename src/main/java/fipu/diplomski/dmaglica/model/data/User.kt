package fipu.diplomski.dmaglica.model.data

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val notificationOptions: NotificationOptions?,
    val role: Role,
    val lastKnownLatitude: Double?,
    val lastKnownLongitude: Double?
)
