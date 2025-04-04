package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "venue_images")
class VenueImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    var name: String = ""

    var venueId: Int = 0

    @Lob
    @Column(name = "image_data", length = 1000)
    var imageData: ByteArray = ByteArray(0)
}