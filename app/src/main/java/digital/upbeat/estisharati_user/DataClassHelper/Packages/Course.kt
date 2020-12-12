package digital.upbeat.estisharati_user.DataClassHelper.Packages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(
    val description: String,
    val id: Int,
    val image: String,
    val image_path: String,
    val name: String,
    val price: Int,
    val rate: String,
    val video_path: String
) : Parcelable