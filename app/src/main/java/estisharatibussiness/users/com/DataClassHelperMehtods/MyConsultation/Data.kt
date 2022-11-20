package estisharatibussiness.users.com.DataClassHelperMehtods.MyConsultation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    var audio: Boolean,
    val category_id: String,
    val category_name: String,
    var chat: Boolean,
    val consultant_id: String,
    val image: String,
    val image_path: String,
    val name: String,
    var video: Boolean,
    var preview_video: String
): Parcelable