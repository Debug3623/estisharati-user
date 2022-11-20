package estisharatibussiness.users.com.UserInterfaces

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import estisharatibussiness.users.com.AdapterClasses.PaymentMethodAdapter
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.PaymentMethodList.Data
import estisharatibussiness.users.com.DataClassHelperMehtods.PaymentMethodList.PMResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import estisharatibussiness.users.com.UtilsClasses.alertActionClickListner
import kotlinx.android.synthetic.main.activity_payment_methods.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PaymentMethods : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var pmResponse : PMResponse = PMResponse("","",arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_methods)
        initViews()
        clickEvents()
        InitializeRecyclerview()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PaymentMethods)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@PaymentMethods)
        dataUser = sharedPreferencesHelper.logInUser
    }

    override fun onStart() {
        if (helperMethods.isConnectingToInternet) {
            paymentMethodListApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
        super.onStart()
    }
    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        proceed.setOnClickListener {
            startActivity(Intent(this@PaymentMethods, ThanksPageActivity::class.java))
        }
        add_payment_method.setOnClickListener {
            val intent = Intent(this@PaymentMethods, ActivityAddPaymentActivity::class.java)
            intent.putExtra("click_from", "add")
            intent.putExtra("card_paypal", "")

            startActivity(intent)
        }
    }

    fun InitializeRecyclerview() {

        payment_method_recycler.setHasFixedSize(true)
        payment_method_recycler.removeAllViews()
        payment_method_recycler.layoutManager = LinearLayoutManager(this@PaymentMethods)
        payment_method_recycler.adapter = PaymentMethodAdapter(this@PaymentMethods, this@PaymentMethods, pmResponse.data)
    }

    fun cardAccountRemovePopup(payment: Data,messageStr: String) {
        helperMethods.showAlertDialog(this@PaymentMethods, object : alertActionClickListner {
            override fun onActionOk() {
                if (helperMethods.isConnectingToInternet) {
                    deletePaymentMethodListApiCall(payment.id!!)
                } else {
                    helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                }
            }

            override fun onActionCancel() {
            }
        }, "Remove", messageStr, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
    }

    fun deletePaymentMethodListApiCall( paymentMethodId:String) {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.DELETE_PAYMENTMETHOD__API_CALL("Bearer ${dataUser.access_token}",paymentMethodId,paymentMethodId)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val message = jsonObject.getString("data")
                                helperMethods.showToastMessage(message)
                                paymentMethodListApiCall()
                            } else {
                                val message = jsonObject.getString("message")
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup("Alert", message)
                            }
                        } catch (e: JSONException) {
                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun paymentMethodListApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.PAYMENTMETHOD_LIST_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            pmResponse = Gson().fromJson(response.body()!!.string(), PMResponse::class.java)
                            if (pmResponse.status.equals("200")) {
                                InitializeRecyclerview()
                            } else {
                               if (helperMethods.checkTokenValidation(pmResponse.status,pmResponse. message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup("Alert", pmResponse.message)
                            }
                        } catch (e: JSONException) {
                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.d("body", "Body Empty")
                    }
                } else {
                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
                    Log.d("body", "Not Successful")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
}