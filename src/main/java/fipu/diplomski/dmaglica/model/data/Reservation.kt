package fipu.diplomski.dmaglica.model.data

import java.time.LocalDateTime

data class Reservation(
    val reservationId: Int,
    val userId: Int,
    val venueId: Int,
    val datetime: LocalDateTime,
    val numberOfGuests: Int,
)
