package estisharatibussiness.users.com.DataClassHelperMehtods.PaymentMethodList

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethods(val id: String, val payment_method: String, val title: String) : Parcelable