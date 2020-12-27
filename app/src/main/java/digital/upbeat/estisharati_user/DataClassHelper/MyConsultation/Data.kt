package digital.upbeat.estisharati_user.DataClassHelper.MyConsultation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    var audio: Boolean,
    val category_id: Int,
    val category_name: String,
    var chat: Boolean,
    val consultant_id: String,
    val image: String,
    val image_path: String,
    val name: String,
    var video: Boolean
): Parcelable