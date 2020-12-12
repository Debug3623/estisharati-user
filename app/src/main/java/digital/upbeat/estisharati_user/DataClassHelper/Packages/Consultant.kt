package digital.upbeat.estisharati_user.DataClassHelper.Packages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Consultant(
    val consultant_price: String,
    val course_count: Int,
    val id: Int,
    val image: String,
    val image_path: String,
    val job_title: String,
    val name: String,
    val rate: Int
) : Parcelable