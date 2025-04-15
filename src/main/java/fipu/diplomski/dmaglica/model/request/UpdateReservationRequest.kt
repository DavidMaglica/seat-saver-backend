package fipu.diplomski.dmaglica.model.request

data class UpdateReservationRequest(
    val userEmail: String,
    val reservationId: Int,
    val reservationDate: String? = null,
    val numberOfPeople: Int? = null,
)
