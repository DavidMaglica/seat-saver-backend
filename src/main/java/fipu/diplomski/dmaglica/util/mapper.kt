package fipu.diplomski.dmaglica.util

import fipu.diplomski.dmaglica.model.data.*
import fipu.diplomski.dmaglica.repo.entity.*

fun NotificationOptionsEntity.toDto() = NotificationOptions(
    isPushNotificationsEnabled = this.pushNotificationsEnabled,
    isEmailNotificationsEnabled = this.emailNotificationsEnabled,
    isLocationServicesEnabled = this.locationServicesEnabled,
)

fun ReservationEntity.toDto() = Reservation(
    id = this.id,
    userId = this.userId,
    venueId = this.venueId,
    datetime = this.datetime,
    numberOfGuests = this.numberOfGuests,
)

fun VenueEntity.toDto() = Venue(
    id = this.id,
    name = this.name,
    location = this.location,
    workingHours = this.workingHours,
    maximumCapacity = this.maximumCapacity,
    availableCapacity = this.availableCapacity,
    averageRating = this.averageRating,
    venueTypeId = this.venueTypeId,
    description = this.description,
)

fun VenueRatingEntity.toDto() = VenueRating(
    id = this.id,
    venueId = this.venueId,
    rating = this.rating,
    username = this.username,
    comment = this.comment,
)

fun VenueTypeEntity.toDto() = VenueType(
    id = this.id,
    type = this.type,
)
