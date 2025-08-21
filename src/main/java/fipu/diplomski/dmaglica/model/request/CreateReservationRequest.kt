package fipu.diplomski.dmaglica.model.request

import java.time.LocalDateTime

data class CreateReservationRequest(
    val userId: Int? = null,
    val userEmail: String? = null,
    val venueId: Int,
    val reservationDate: LocalDateTime,
    val numberOfGuests: Int
)
