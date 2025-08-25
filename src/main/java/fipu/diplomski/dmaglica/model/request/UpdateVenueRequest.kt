package fipu.diplomski.dmaglica.model.request

data class UpdateVenueRequest(
    val name: String? = null,
    val location: String? = null,
    val description: String? = null,
    val typeId: Int? = null,
    val workingDays: List<Int>? = null,
    val workingHours: String? = null,
    val maximumCapacity: Int? = null,
)