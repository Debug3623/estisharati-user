package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.PaymentMethodAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.FaqDetails.FAQResponse
import digital.upbeat.estisharati_user.DataClassHelper.PaymentMethodList.PMResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.alertActionClickListner
import kotlinx.android.synthetic.main.activity_payment_methods.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PaymentMethods : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var pmResponse : PMResponse = PMResponse("",arrayListOf())
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
            PAYMENTMETHOD_LIST_API_CALL()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
        super.onStart()
    }
    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        proceed.setOnClickListener {
            startActivity(Intent(this@PaymentMethods, ThanksPage::class.java))
        }
        add_payment_method.setOnClickListener {
            val intent = Intent(this@PaymentMethods, AddPaymentMethod::class.java)
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

    fun cardAccountRemovePopup(messageStr: String) {
        helperMethods.showAlertDialog(this@PaymentMethods, object : alertActionClickListner {
            override fun onActionOk() {
            }

            override fun onActionCancel() {
            }
        }, "Remove", messageStr, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
    }

    fun PAYMENTMETHOD_LIST_API_CALL() {
        helperMethods.showProgressDialog("Please wait while loading...")
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
                                val jsonObject = JSONObject(response.body()!!.string())
                                val message = jsonObject.getString("message")
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
}