package fipu.diplomski.dmaglica.model.request

data class CreateVenueRequest(
    val name: String,
    val location: String,
    val description: String,
    val typeId: Int,
    val workingHours: String,
    val maximumCapacity: Int,
    val availableCapacity: Int,
)
