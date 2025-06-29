package fipu.diplomski.dmaglica.controller

import fipu.diplomski.dmaglica.model.data.Reservation
import fipu.diplomski.dmaglica.model.request.CreateReservationRequest
import fipu.diplomski.dmaglica.model.request.UpdateReservationRequest
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.service.ReservationService
import fipu.diplomski.dmaglica.util.Paths
import org.springframework.web.bind.annotation.*

/**
 * REST controller for managing venue reservations.
 *
 * Provides endpoints for creating, retrieving, updating, and deleting reservations.
 * All endpoints are prefixed with `/api/reservations` (defined in [Paths.RESERVATION]).
 *
 * @property reservationService Service handling reservation business logic
 */
@RestController
@RequestMapping(Paths.RESERVATION)
class ReservationController(private val reservationService: ReservationService) {

    /**
     * Creates a new reservation after validating availability.
     *
     * Performs the following validations:
     * 1. Checks if user exists
     * 2. Checks if venue exists
     * 3. Verifies capacity availability for the requested time window
     *    (30 minutes before and after requested time)
     *
     * Uses SERIALIZABLE transaction isolation to prevent concurrent booking conflicts.
     *
     * @param request [CreateReservationRequest] Required reservation details:
     *   - userId: Valid existing user id
     *   - venueId: Valid existing venue id
     *   - reservationDate: Booking datetime (used for time window calculation)
     *   - numberOfPeople: Guest count (must not exceed remaining capacity)
     * @return [BasicResponse] with:
     *   - success: true if booked successfully
     *   - message: Detailed status message
     * @throws fipu.diplomski.dmaglica.exception.VenueNotFoundException if venue doesn't exist
     */
    @PostMapping(Paths.CREATE_RESERVATION)
    fun createReservation(
        @RequestBody request: CreateReservationRequest
    ): BasicResponse = reservationService.create(request)

    /**
     * Retrieves all active reservations for a specific user.
     *
     * Returns an empty list if:
     * - The user doesn't exist
     * - The user has no reservations
     *
     * @param userId id of the user to query
     * @return List of [Reservation] objects (empty if none found)
     *
     */
    @GetMapping(Paths.GET_RESERVATIONS)
    fun getReservations(
        @RequestParam("userId") userId: Int
    ): List<Reservation> = reservationService.getAll(userId)

    /**
     * Updates an existing reservation after validating the request.
     *
     * Performs the following validations:
     * 1. Verifies if user exists
     * 2. Checks reservation exists and belongs to user
     * 3. Validates request contains at least one modification
     * 4. Confirms venue exists
     *
     * @param request Update details containing:
     *   - userId: id of user making the request (must own reservation)
     *   - reservationId: id of reservation to update
     *   - venueId: id of venue (for validation)
     *   - reservationDate: New datetime (optional)
     *   - numberOfPeople: New guest count (optional)
     * @return BasicResponse with:
     *   - success: true if update succeeded
     *   - message: Detailed status including:
     *     - Error reasons for failure
     *     - Success confirmation
     * @throws fipu.diplomski.dmaglica.exception.UserNotFoundException if user doesn't exist
     * @throws fipu.diplomski.dmaglica.exception.ReservationNotFoundException if reservation doesn't exist
     * @throws fipu.diplomski.dmaglica.exception.VenueNotFoundException if venue doesn't exist
     */
    @PatchMapping(Paths.UPDATE_RESERVATION)
    fun updateReservation(
        @RequestBody request: UpdateReservationRequest
    ): BasicResponse = reservationService.update(request)

    /**
     * Cancels and deletes an existing reservation after validation.
     *
     * Performs the following validations:
     * 1. Verifies if the user exists
     * 2. Confirms the reservation exists
     * 3. Validates the venue exists
     *
     * @note: The reservation must belong to the user making the request.
     *
     * @param userId id of the user making the cancellation request
     * @param reservationId id of the reservation to cancel
     * @param venueId id of the related venue (for validation)
     * @return BasicResponse with:
     *   - success: true if deletion succeeded
     *   - message: Detailed status including:
     *     - Error reasons for failure
     *     - Success confirmation
     * @throws fipu.diplomski.dmaglica.exception.UserNotFoundException if user doesn't exist
     * @throws fipu.diplomski.dmaglica.exception.ReservationNotFoundException if reservation doesn't exist
     * @throws fipu.diplomski.dmaglica.exception.VenueNotFoundException if venue doesn't exist
     */
    @DeleteMapping(Paths.DELETE_RESERVATION)
    fun deleteReservation(
        @RequestParam("userId") userId: Int,
        @RequestParam("reservationId") reservationId: Int,
        @RequestParam("venueId") venueId: Int
    ): BasicResponse = reservationService.delete(userId, reservationId, venueId)
}