package fipu.diplomski.dmaglica.repo.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "working_days",
    uniqueConstraints = [UniqueConstraint(columnNames = ["venue_id", "day_of_week"])]
)
class WorkingDaysEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0

    var venueId: Int = 0

    var dayOfWeek: Int = 0
}
