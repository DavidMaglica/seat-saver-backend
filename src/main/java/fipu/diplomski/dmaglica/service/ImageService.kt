package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.ImageDataException
import fipu.diplomski.dmaglica.model.response.BasicResponse
import fipu.diplomski.dmaglica.repo.MenuImageRepository
import fipu.diplomski.dmaglica.repo.VenueImageRepository
import fipu.diplomski.dmaglica.repo.entity.MenuImageEntity
import fipu.diplomski.dmaglica.repo.entity.VenueImageEntity
import fipu.diplomski.dmaglica.util.compressImage
import fipu.diplomski.dmaglica.util.decompressImage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Service
class ImageService(
    private val venueImageRepository: VenueImageRepository,
    private val menuImageRepository: MenuImageRepository,
) {

    companion object {
        private val logger = KotlinLogging.logger(ImageService::class.java.name)
    }

    @Transactional(readOnly = true)
    fun getVenueImages(venueId: Int): List<String> {
        val venueImages = venueImageRepository.findByVenueId(venueId)

        if (venueImages.isEmpty()) {
            logger.warn { "No menu images found for venue id: $venueId" }
            return emptyList()
        }

        return venueImages.map {
            Base64.getEncoder().encodeToString(decompressImage(it.imageData))
        }
    }

    @Transactional(readOnly = true)
    fun getMenuImage(venueId: Int): List<String> {
        val menuImages = menuImageRepository.findByVenueId(venueId)

        if (menuImages.isEmpty()) {
            logger.warn { "No menu images found for venue id: $venueId" }
            return emptyList()
        }

        return menuImages.map {
            Base64.getEncoder().encodeToString(decompressImage(it.imageData))
        }
    }

    @Transactional
    fun uploadVenueImage(venueId: Int, file: MultipartFile): BasicResponse {
        validateImage(file)

        try {
            val venueImageEntity = VenueImageEntity().apply {
                this.venueId = venueId
                this.name = file.originalFilename!!
                this.imageData = compressImage(file.bytes)
            }
            venueImageRepository.save(venueImageEntity)
        } catch (e: Exception) {
            logger.error(e) { "Error while saving venue image: ${e.message}" }
            return BasicResponse(
                false,
                "Error while saving venue image. Please try again later."
            )
        }

        return BasicResponse(true, "Image '${file.originalFilename}' uploaded successfully.")
    }

    @Transactional
    fun uploadMenuImage(venueId: Int, file: MultipartFile): BasicResponse {
        validateImage(file)

        try {
            val menuImageEntity = MenuImageEntity().apply {
                this.venueId = venueId
                this.name = file.originalFilename!!
                this.imageData = compressImage(file.bytes)
            }
            menuImageRepository.save(menuImageEntity)
        } catch (e: Exception) {
            logger.error { "Error while saving menu image: ${e.message}" }
            return BasicResponse(
                false,
                "Error while saving menu image. Please try again later."
            )
        }

        return BasicResponse(true, "Image '${file.originalFilename}' uploaded successfully.")
    }

    private fun validateImage(file: MultipartFile) {
        if (file.isEmpty) throw ImageDataException("File is empty", null)

        if (file.size > 5 * 1024 * 1024) throw ImageDataException("File size exceeds the limit of 5MB", null)

        if (file.contentType != "image/jpeg" && file.contentType != "image/png") throw ImageDataException(
            "Invalid file type. Only JPEG and PNG are allowed",
            null
        )

        if (file.originalFilename == null) throw ImageDataException("File name is null", null)
    }
}
