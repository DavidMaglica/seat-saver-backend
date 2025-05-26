package fipu.diplomski.dmaglica.exception

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<String> {
        logger.error("Exception occurred: ${ex.message}")
        return ResponseEntity.internalServerError().body("Something went wrong: ${ex.message}")
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException): ResponseEntity<String> {
        logger.error("Entity not found occurred: ${ex.message}. Cause: ${ex.cause}")
        return ResponseEntity.internalServerError().body("Entity not found exception occurred: ${ex.message}")
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<String> {
        logger.error("User not found: ${ex.message}. Cause: ${ex.cause}")
        return ResponseEntity.internalServerError().body("User not found: ${ex.message}")
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<String> {
        logger.error("User already exists: ${ex.message}. Cause: ${ex.cause}")
        return ResponseEntity.internalServerError().body("User already exists: ${ex.message}")
    }

    @ExceptionHandler(ImageDataException::class)
    fun handleImageDataException(ex: ImageDataException): ResponseEntity<String> {
        logger.error("Image data exception: ${ex.message}. Cause: ${ex.cause}")
        return ResponseEntity.internalServerError().body("Exception while handling image data: ${ex.message}")
    }

    @ExceptionHandler(VenueNotFoundException::class)
    fun handleVenueNotFoundException(ex: VenueNotFoundException): ResponseEntity<String> {
        logger.error("Venue not found: ${ex.message}. Cause: ${ex.cause}")
        return ResponseEntity.internalServerError().body("Venue not found: ${ex.message}")
    }

    @ExceptionHandler(ReservationNotFoundException::class)
    fun handleReservationNotFoundException(ex: ReservationNotFoundException): ResponseEntity<String> {
        logger.error("Reservation not found: ${ex.message}. Cause: ${ex.cause}")
        return ResponseEntity.internalServerError().body("Reservation not found: ${ex.message}")
    }
}
