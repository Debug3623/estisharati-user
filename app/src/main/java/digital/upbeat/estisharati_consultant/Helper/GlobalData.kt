package digital.upbeat.estisharati_user.Helper

object GlobalData {
    const val PICK_IMAGE_GALLERY = 1
    const val PICK_IMAGE_CAMERA = 2
    var LanguageCode = "en"
    val BaseUrl = "https://super-servers.com/estisharati/api/v1/$LanguageCode/"

    var profileUpdate=false
    var FcmToken=""
    const val DOC = "application/msword"
    const val DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    const val IMAGE = "image/*"
    const val AUDIO = "audio/*"
    const val TEXT = "text/*"
    const val PDF = "application/pdf"
    const val CSV = "application/csv"
    const val XLS = "application/vnd.ms-excel"
}