package digital.upbeat.estisharati_user.networkPayment

import android.os.AsyncTask
import android.util.Log
import digital.upbeat.estisharati_user.UI.PackagesSelection
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.text.NumberFormat
import java.util.*

class NetwrokPayment {
    fun createOrder(packagesSelection: PackagesSelection, Token: String, Price: String, language: String) {
        Log.d("payment_amount", Price)
        var transactionAmount = Price.replace(".", "")
        transactionAmount = transactionAmount.replace("٫", "")
        transactionAmount = NumberFormat.getNumberInstance(Locale.US).format(transactionAmount.toLong())
        transactionAmount = transactionAmount.replace(",", "")
        transactionAmount = transactionAmount.replace(".", "")
        var amount = transactionAmount
        //    final String amount = "10" + "0";
        Log.d("payment_amount", "$amount  $language")
        var paymentResponse = ""

        class OrderClass : AsyncTask<String?, String?, String?>() {
            protected override fun doInBackground(vararg strings: String?): String? {
                val client = OkHttpClient()
                val amountJsonObject = JSONObject()
                val Object = JSONObject()
                val billingAddress = JSONObject()
                val shippingAddress = JSONObject()
                try {
                    amountJsonObject.put("currencyCode", "AED")
                    amountJsonObject.put("value", amount)
                    Object.put("action", "SALE")
                    Object.put("amount", amountJsonObject)
                    Object.put("emailAddress", packagesSelection.sharedPreferencesHelper.logInUser.email)
                    Object.put("language", language)
                    Object.put("merchantOrderReference", "Estisharati-" + packagesSelection.sharedPreferencesHelper.logInUser.id)
                    billingAddress.put("firstName", packagesSelection.sharedPreferencesHelper.logInUser.fname)
                    billingAddress.put("lastName", "" + packagesSelection.sharedPreferencesHelper.logInUser.lname)
                    billingAddress.put("address1", "101 Aljaber Jewellery Building, Zayed the 1st street, Khaldiyah, Abu Dhabi, UAE")
                    billingAddress.put("city", "Abu Dhabi")
                    billingAddress.put("countryCode", "+971")
                    shippingAddress.put("firstName", packagesSelection.sharedPreferencesHelper.logInUser.fname)
                    shippingAddress.put("lastName", "" + packagesSelection.sharedPreferencesHelper.logInUser.lname)
                    shippingAddress.put("address1", "101 Aljaber Jewellery Building, Zayed the 1st street, Khaldiyah, Abu Dhabi, UAE")
                    shippingAddress.put("city", "Abu Dhabi")
                    shippingAddress.put("countryCode", "+971")
                    Object.put("billingAddress", billingAddress)
                    Object.put("shippingAddress", shippingAddress)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.d("requsetCurrentcyCode", Object.toString())
                val mediaType = MediaType.parse("application/vnd.ni-payment.v2+json")
                val body = RequestBody.create(mediaType, Object.toString())
                val request = Request.Builder().url("https://api-gateway.ngenius-payments.com/transactions/outlets/e477951c-6e67-40f2-818c-434cab1f995d/orders")
//                    .url("https://api-gateway.sandbox.ngenius-payments.com/transactions/outlets/bf13a29c-b0fb-45a4-b329-529ffba307f5/orders")
                    .post(body).addHeader("Authorization", "Bearer $Token").addHeader("Content-Type", "application/vnd.ni-payment.v2+json").addHeader("Accept", "application/vnd.ni-payment.v2+json").build()
                var response: Response
                run {
                    try {
                        response = client.newCall(request).execute()
                        paymentResponse = response.body()!!.string()
                        Log.d("response", paymentResponse)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                result?.let {
                    Log.d("onPostExecute", it)
                }
                packagesSelection.lunchPaymentGateway(paymentResponse)
            }
        }

        val c = OrderClass()
        c.execute()
    }

    fun getToken(packagesSelection: PackagesSelection) {
        val call = NetworkApiClient.client.getTockenForPayment("client_credentials")
        call.enqueue(object : Callback<AuthResponce?> {
            override fun onResponse(call: Call<AuthResponce?>, resultResponse: retrofit2.Response<AuthResponce?>) {
                val response = resultResponse.body()
                if (response != null) {
                    packagesSelection.callOrderFroPaymentAPI(response.access_token)
                } else {
                    packagesSelection.tokenError("Unable to get token !");
                }
            }

            override fun onFailure(call: Call<AuthResponce?>, t: Throwable) {
                packagesSelection.tokenError("Token onFailure !");
            }
        })
    }
}