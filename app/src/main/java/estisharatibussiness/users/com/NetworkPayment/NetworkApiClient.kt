package estisharatibussiness.users.com.networkPayment

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkApiClient {
    var service: NetworkPaymentApi?=null

    val client: NetworkPaymentApi
        get() {
            if (service == null) {
                val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                val httpClient = OkHttpClient.Builder()
                //    }
                httpClient.addInterceptor { chain: Interceptor.Chain ->
                    val original = chain.request()
                    // Request customization: add request headers
                    val requestBuilder = original.newBuilder().addHeader("Authorization", "Basic YmMwNGU5MGYtNWQ5My00NTcwLWI1YjItYTBhNGZjNGEzZDBhOjE1ZGFmN2MzLWJiOTctNGExYy04MDFhLWE4ZTNhNGY1Y2Q4Ng==")
                        .addHeader("Content-Type", "application/vnd.ni-identity.v1+json")
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                httpClient.connectTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES)
//                val baseUrl = "https://api-gateway.sandbox.ngenius-payments.com/identity/auth/"
                val baseUrl = "https://api-gateway.ngenius-payments.com/identity/auth/"
                val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson)).client(httpClient.build()).build()
                service = retrofit.create(NetworkPaymentApi::class.java)
            }
            return service!!
        }
}