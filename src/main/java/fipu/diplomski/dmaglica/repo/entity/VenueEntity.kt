package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "venues", indexes = [Index(name = "idx_venue_name_lower", columnList = "name")])
class VenueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    var ownerId: Int = 0

    var name: String = ""

    var location: String = ""

    var workingHours: String = ""

    var maximumCapacity: Int = 0

    var availableCapacity: Int = 0

    var averageRating: Double = 0.0

    var venueTypeId: Int = 0

    var description: String? = ""
}
