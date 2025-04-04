package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(name = "menu_images")
class MenuImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    var name: String = ""

    var venueId: Int = 0

    @Lob
    @Column(name = "image_data", length = 1000)
    var imageData: ByteArray = ByteArray(0)
}