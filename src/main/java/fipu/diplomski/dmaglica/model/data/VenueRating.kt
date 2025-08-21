package fipu.diplomski.dmaglica.model.data

data class VenueRating(
    val id: Int,
    val venueId: Int,
    val rating: Double,
    val username: String,
    val comment: String?,
)
