package estisharatibussiness.users.com.networkPayment.NetworkModel

import com.google.gson.annotations.SerializedName

data class LinksX(@SerializedName("cnp:payment-link") val cnpPaymentLink: CnpPaymentLink, @SerializedName("merchant-brand") val merchantBrand: MerchantBrand, val payment: PaymentX, @SerializedName("payment-authorization") val paymentAuthorization: PaymentAuthorization, val self: SelfX, @SerializedName("tenant-brand") val tenantBrand: TenantBrand)