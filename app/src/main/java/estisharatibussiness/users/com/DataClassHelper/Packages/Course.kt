package estisharatibussiness.users.com.DataClassHelper.Packages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Course(
    val description: String,
    val id: String,
    val image: String,
    val image_path: String,
    val name: String,
    val price: Int,
    val rate: String,
    val video_path: String
): Parcelable