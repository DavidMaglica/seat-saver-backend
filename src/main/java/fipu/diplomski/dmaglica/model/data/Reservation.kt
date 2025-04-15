package fipu.diplomski.dmaglica.model.data

data class Reservation(
    val reservationId: Int,
    val userId: Int,
    val venueId: Int,
    val datetime: String,
    val numberOfGuests: Int,
)
