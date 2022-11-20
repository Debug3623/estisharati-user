package estisharatibussiness.users.com.DataClassHelperMehtods.PaymentMethodList

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(val id:String?,val details: Details?, val payment_method: String?) : Parcelable