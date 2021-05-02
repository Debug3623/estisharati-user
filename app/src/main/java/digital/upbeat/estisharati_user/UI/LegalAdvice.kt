package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.LegalAdviceAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Consultant.ConsultantResponse
import digital.upbeat.estisharati_user.DataClassHelper.Consultant.Data
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_legal_advice.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class
LegalAdvice : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    lateinit var consultantsResponse: ConsultantResponse
    var consultantsArrayList: ArrayList<Data> = arrayListOf()
    var sortingOptionsArraylist: ArrayList<String> = arrayListOf()
    var category_id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_legal_advice)

        initViews()
        clickEvents()
        category_id = intent.getStringExtra("category_id").toString()
        if (helperMethods.isConnectingToInternet) {
            allConsultantsApiCall(category_id, "Default")
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
        if (GlobalData.isThingInitialized()) {
            notificationCount.text = GlobalData.homeResponse.notification_count
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        notificationLayout.setOnClickListener {
            startActivity(Intent(this@LegalAdvice, Notifications::class.java))
        }
        searchConsultant.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (::consultantsResponse.isInitialized) {
                    InitializeRecyclerview()
                }
            }
        })
    }

    fun InitializeRecyclerview() {
        consultantsArrayList.clear()
        if (sortingOptionsArraylist.isEmpty()) {
            sortingOptionsArraylist = consultantsResponse.sorting_options
            initializeFilterSpinner()
        }
        for (consultants in consultantsResponse.data) {
            if (searchConsultant.text.toString().equals("")) {
                consultantsArrayList.add(consultants)
            } else if (consultants.user.name.contains(searchConsultant.text.toString(), true)) {
                consultantsArrayList.add(consultants)
            }
        }


        if (consultantsArrayList.size > 0) {
            legal_advice_recycler.visibility = View.VISIBLE
            legalEmptyLayout.visibility = View.GONE
        } else {
            legal_advice_recycler.visibility = View.GONE
            legalEmptyLayout.visibility = View.VISIBLE
            legal_errorText.text = getString(R.string.there_is_no_legal_advice_available)
        }

        legal_advice_recycler.setHasFixedSize(true)
        legal_advice_recycler.removeAllViews()
        legal_advice_recycler.layoutManager = LinearLayoutManager(this@LegalAdvice)
        legal_advice_recycler.adapter = LegalAdviceAdapter(this@LegalAdvice, this@LegalAdvice, consultantsArrayList)
    }

    fun initializeFilterSpinner() {
        val typeface = ResourcesCompat.getFont(this@LegalAdvice, R.font.almarai_regular)
        val adapter = ArrayAdapter(this@LegalAdvice, R.layout.support_simple_spinner_dropdown_item, sortingOptionsArraylist)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        legal_filter_spinner.adapter = adapter
        legal_filter_spinner.setSelection(0, true)
        val v2 = legal_filter_spinner.selectedView
        (v2 as TextView).textSize = 15f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@LegalAdvice, R.color.white))
        legal_filter_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                (view as TextView).textSize = 15f
                view.typeface = typeface
                view.setTextColor(ContextCompat.getColor(this@LegalAdvice, R.color.white))
                allConsultantsApiCall(category_id, sortingOptionsArraylist.get(legal_filter_spinner.selectedItemPosition))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun allConsultantsApiCall(category_id: String, sortby: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.ALL_CONSULTANTS_API_CALL("Bearer ${dataUser.access_token}", category_id, sortby)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            consultantsResponse = Gson().fromJson(response.body()!!.string(), ConsultantResponse::class.java)
                            Log.d("HRes", response.body()!!.string())
                            if (consultantsResponse.status.equals("200")) {
                                InitializeRecyclerview()
                            } else {
                                if (helperMethods.checkTokenValidation(consultantsResponse.status,consultantsResponse.message)) {
                                    finish()
                                    return
                                }
                                InitializeRecyclerview()
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
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
}
