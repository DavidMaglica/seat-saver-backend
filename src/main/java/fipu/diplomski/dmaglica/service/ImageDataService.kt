package fipu.diplomski.dmaglica.service

import fipu.diplomski.dmaglica.exception.ImageDataException
import fipu.diplomski.dmaglica.model.BasicResponse
import fipu.diplomski.dmaglica.repo.ImageDataRepository
import fipu.diplomski.dmaglica.repo.entity.ImageDataEntity
import fipu.diplomski.dmaglica.util.compressImage
import fipu.diplomski.dmaglica.util.decompressImage
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile


@Service
class ImageDataService(
    private val imageDataRepository: ImageDataRepository,
) {

    fun uploadImage(venueId: Int, file: MultipartFile): BasicResponse {
        validateImage(file)

        imageDataRepository.saveAndFlush(
            ImageDataEntity().also {
                it.id
                it.venueId = venueId
                it.name = file.originalFilename!!
                it.imageData = compressImage(file.bytes)
            }
        )

        return BasicResponse(true, "Image '${file.originalFilename}' uploaded successfully")
    }

    @Transactional
    fun getImagesForVenue(venueId: Int, venueName: String): List<ByteArray> {
        val imageDataEntities = imageDataRepository.findByVenueId(venueId)

        if (imageDataEntities.isEmpty()) throw ImageDataException("Images for venue $venueName do not exist", null)

        val images = mutableListOf(ByteArray(0))
        imageDataEntities.map {
            images.add(decompressImage(it.imageData))
        }

        return images
    }

    private fun validateImage(file: MultipartFile) {
        if (file.isEmpty) {
            throw ImageDataException("File is empty", null)
        }
        if (file.size > 5 * 1024 * 1024) {
            throw ImageDataException("File size exceeds the limit of 5MB", null)
        }
        if (file.contentType != "image/jpeg" && file.contentType != "image/png") {
            throw ImageDataException("Invalid file type. Only JPEG and PNG are allowed", null)
        }
        if (file.originalFilename == null) {
            throw ImageDataException("File name is null", null)
        }
    }


}