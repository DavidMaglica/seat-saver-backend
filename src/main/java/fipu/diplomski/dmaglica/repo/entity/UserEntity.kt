package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var username: String = ""
    var email: String = ""
    var password: String = ""
    var lastKnownLatitude: Double? = null
    var lastKnownLongitude: Double? = null
    var roleId: Int = 0
}