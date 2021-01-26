package digital.upbeat.estisharati_consultant.DataClassHelper.Notification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
data class Data(
    val body: String,
    val created_at: String,
    val details: ArrayList<Detail>?,
    val id: String,
    val title: String,
    val type: String
)