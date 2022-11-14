package estisharatibussiness.users.com.networkPayment

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface NetworkPaymentApi {
    @POST("access-token") fun getTockenForPayment(@Query("grant_type") grant_type: String): Call<AuthResponce>
}