package fipu.diplomski.dmaglica.model.request

import java.time.LocalDateTime

data class CreateReservationRequest(
    val userId: Int,
    val venueId: Int,
    val reservationDate: LocalDateTime,
    val numberOfPeople: Int
)
