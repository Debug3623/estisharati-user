package estisharatibussiness.users.com.networkPayment.NetworkModel

import com.google.gson.annotations.SerializedName

data class Links(
    val curies: List<Cury>,
    @SerializedName("payment:card")

    val paymentCard: PaymentCard,
    val self: Self
)