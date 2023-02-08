package estisharati.bussiness.eshtisharati_consultants.DataClassHelper.MySubsribers

data class Data(
    val audio: Boolean,
    val audio_balance: Int,
    val chat: Boolean,
    val chat_balance: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val user_id: String,
    val video: Boolean,
    val video_balance: Int
)