package digital.upbeat.estisharati_user.DataClassHelper.PaymentMethodList

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Data(val details: Details?, val payment_method: String?) : Parcelable