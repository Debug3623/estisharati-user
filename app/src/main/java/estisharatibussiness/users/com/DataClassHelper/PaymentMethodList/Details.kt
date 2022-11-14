package estisharatibussiness.users.com.DataClassHelper.PaymentMethodList

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Details(
    val cardno: String?,
    val cvv: String?,
    val email: String?,
    val expiry: String?,
    val name: String?,
    val paypal: String?
): Parcelable