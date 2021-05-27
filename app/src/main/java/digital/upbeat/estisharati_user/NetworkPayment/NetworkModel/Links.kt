package digital.upbeat.estisharati_user.networkPayment.NetworkModel

import com.google.gson.annotations.SerializedName

data class Links(
    val curies: List<Cury>,
    @SerializedName("payment:card")

    val paymentCard: PaymentCard,
    val self: Self
)