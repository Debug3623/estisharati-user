package digital.upbeat.estisharati_consultant.DataClassHelper.Notification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
data class User(
    val course_count: String,
    val id: String,
    val image: String,
    val image_path: String,
    val name: String,
    val rate: String
)