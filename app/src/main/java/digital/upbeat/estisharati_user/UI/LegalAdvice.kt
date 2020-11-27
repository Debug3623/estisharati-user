package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.LegalAdviceAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Consultant.Consultant
import digital.upbeat.estisharati_user.DataClassHelper.Consultant.ConsultantsResponse
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_legal_advice.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class LegalAdvice : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    lateinit var consultantsResponse: ConsultantsResponse
    var consultantsArrayList: ArrayList<Consultant> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_advice)

        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            allConsultantsApiCall(intent.getStringExtra("category_id"))
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
        if (!intent.getStringExtra("category_name").equals("")) {
            actionBarTitle.text = intent.getStringExtra("category_name")
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@LegalAdvice)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@LegalAdvice)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        searchConsultant.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                InitializeRecyclerview()
            }
        })
    }

    fun InitializeRecyclerview() {
        consultantsArrayList.clear()
        for (consultants in consultantsResponse.data) {
            for (consultant in consultants.consultants) {
//                if (searchConsultant.text.toString().equals("")) {
                    consultantsArrayList.add(consultant)
//                } else {
//                    if (consultant.user.name.contains(searchConsultant.text.toString())) {
//                        consultantsArrayList.add(consultant)
//                    }
//                }
            }
        }


        legal_advice_recycler.setHasFixedSize(true)
        legal_advice_recycler.removeAllViews()
        legal_advice_recycler.layoutManager = LinearLayoutManager(this@LegalAdvice)
        legal_advice_recycler.adapter = LegalAdviceAdapter(this@LegalAdvice, this@LegalAdvice, consultantsArrayList)
    }

    fun allConsultantsApiCall(category_id: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.ALL_CONSULTANTS_API_CALL("Bearer ${dataUser.access_token}", category_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            consultantsResponse = Gson().fromJson(response.body()!!.string(), ConsultantsResponse::class.java)

                            if (consultantsResponse.status.equals("200")) {
                                InitializeRecyclerview()
                            } else {
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
