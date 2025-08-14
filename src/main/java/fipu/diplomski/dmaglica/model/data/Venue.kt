package fipu.diplomski.dmaglica.model.data

data class Venue(
    val id: Int,
    val name: String,
    val location: String,
    val workingHours: String,
    val maximumCapacity: Int,
    val availableCapacity: Int,
    val averageRating: Double,
    val venueTypeId: Int,
    val description: String?,
)
