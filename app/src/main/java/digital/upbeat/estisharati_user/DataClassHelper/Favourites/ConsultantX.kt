package digital.upbeat.estisharati_user.DataClassHelper.Favourites

data class ConsultantX(
    val id: String,
    val image: String,
    val image_path: String,
    val name: String,
    val chat_fee: String,
    val voice_fee: String,
    val video_fee: String,
    var offer_chat_fee: String,
    var offer_voice_fee: String,
    var offer_video_fee: String,
    val job_title: String,
    val rate: String
)