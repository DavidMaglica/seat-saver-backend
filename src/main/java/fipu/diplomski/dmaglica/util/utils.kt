package fipu.diplomski.dmaglica.util

import fipu.diplomski.dmaglica.exception.ImageDataException
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater
import java.util.zip.Inflater

inline fun <reified T> imageActionWithTryCatch(errorMessage: String, block: () -> T): T = try {
    block()
} catch (e: Exception) {
    throw ImageDataException(errorMessage, e)
}

fun compressImage(data: ByteArray): ByteArray {
    val deflater = Deflater(Deflater.BEST_COMPRESSION)
    deflater.setInput(data)
    deflater.finish()

    val outputStream = ByteArrayOutputStream(data.size)
    val tmp = ByteArray(4 * 1024)

    imageActionWithTryCatch("Error while deflating output stream during image compression") {
        while (!deflater.finished()) {
            val size = deflater.deflate(tmp)
            outputStream.write(tmp, 0, size)
        }
        outputStream.close()
    }

    return outputStream.toByteArray()
}


fun decompressImage(data: ByteArray): ByteArray {
    val inflater = Inflater()
    inflater.setInput(data)
    val outputStream = ByteArrayOutputStream(data.size)
    val tmp = ByteArray(4 * 1024)

    imageActionWithTryCatch("Error while deflating output stream during image decompression") {
        while (!inflater.finished()) {
            val count = inflater.inflate(tmp)
            outputStream.write(tmp, 0, count)
        }
        outputStream.close()
    }

    return outputStream.toByteArray()
}
