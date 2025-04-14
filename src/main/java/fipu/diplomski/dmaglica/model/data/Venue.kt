package fipu.diplomski.dmaglica.model.data

data class Venue(
    var id: Int,
    var name: String,
    var location: String,
    var workingHours: String,
    var rating: Double,
    var venueTypeId: Int,
    var description: String,
)
