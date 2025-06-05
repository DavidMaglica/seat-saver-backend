package fipu.diplomski.dmaglica.model.response

data class DataResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
