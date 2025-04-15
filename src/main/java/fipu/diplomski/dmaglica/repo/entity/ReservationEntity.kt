package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "reservations")
data class ReservationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    var userId: Int = 0,

    var venueId: Int = 0,

    var datetime: String = "",

    var numberOfGuests: Int = 0,
)
