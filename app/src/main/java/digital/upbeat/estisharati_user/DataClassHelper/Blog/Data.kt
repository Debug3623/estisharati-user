package digital.upbeat.estisharati_user.DataClassHelper.Blog

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val title: String
):Parcelable