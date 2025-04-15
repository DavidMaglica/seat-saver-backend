package fipu.diplomski.dmaglica.model.request

data class CreateReservationRequest(
    val userEmail: String,
    val venueId: Int,
    val reservationDate: String,
    val numberOfPeople: Int
)
