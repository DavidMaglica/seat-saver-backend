package fipu.diplomski.dmaglica.model

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val email: String,
    val notificationOptions: NotificationOptions?,
    val role: List<Role>?,
)
