package estisharatibussiness.users.com.DataClassHelper.Packages

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Consultant(
    val id: String,
    val image_path: String,
    val name: String,
): Parcelable