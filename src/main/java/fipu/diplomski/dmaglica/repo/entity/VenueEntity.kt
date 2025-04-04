package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "venues")
class VenueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    var name: String = ""

    var location: String = ""

    var workingHours: String = ""

    var averageRating: Double = 0.0

    var venueTypeId: Int = 0

    var description: String = ""
}
