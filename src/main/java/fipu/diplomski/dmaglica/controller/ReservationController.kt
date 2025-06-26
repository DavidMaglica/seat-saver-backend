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
     * @param request Required reservation details:
     *   - userId: Valid existing user ID
     *   - venueId: Valid existing venue ID
     *   - reservationDate: Booking datetime (used for time window calculation)
     *   - numberOfPeople: Guest count (must not exceed remaining capacity)
     * @return BasicResponse with:
     *   - success: true if booked successfully
     *   - message: Detailed status message
     * @throws VenueNotFoundException if venue doesn't exist
     * @throws DataAccessException if database operation fails
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
     * @param userId ID of the user to query
     * @return List of [Reservation] objects (empty if none found)
     *
     * @implNote This is a read-only operation with no side effects
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
     *   - userId: ID of user making the request (must own reservation)
     *   - reservationId: ID of reservation to update
     *   - venueId: ID of venue (for validation)
     *   - reservationDate: New datetime (optional)
     *   - numberOfPeople: New guest count (optional)
     * @return BasicResponse with:
     *   - success: true if update succeeded
     *   - message: Detailed status including:
     *     - Error reasons for failure
     *     - Success confirmation
     * @throws ReservationNotFoundException if reservation doesn't exist
     * @throws VenueNotFoundException if venue doesn't exist
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
     * Note: The reservation must belong to the user making the request.
     *
     * @param userId ID of the user making the cancellation request
     * @param reservationId ID of the reservation to cancel
     * @param venueId ID of the related venue (for validation)
     * @return BasicResponse with:
     *   - success: true if deletion succeeded
     *   - message: Detailed status including:
     *     - Error reasons for failure
     *     - Success confirmation
     * @throws ReservationNotFoundException if reservation doesn't exist
     * @throws VenueNotFoundException if venue doesn't exist
     */
    @DeleteMapping(Paths.DELETE_RESERVATION)
    fun deleteReservation(
        @RequestParam("userId") userId: Int,
        @RequestParam("reservationId") reservationId: Int,
        @RequestParam("venueId") venueId: Int
    ): BasicResponse = reservationService.delete(userId, reservationId, venueId)
}