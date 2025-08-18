package fipu.diplomski.dmaglica.model.request

import java.time.LocalDateTime

data class UpdateReservationRequest(
    val reservationDate: LocalDateTime? = null,
    val numberOfGuests: Int? = null,
)
