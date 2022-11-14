package estisharatibussiness.users.com.UserInterface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import estisharatibussiness.users.com.Adapter.SurveyListAdapter
import estisharatibussiness.users.com.ApiHelper.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelper.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelper.Login.DataUser
import estisharatibussiness.users.com.DataClassHelper.SurveyList.Data
import estisharatibussiness.users.com.DataClassHelper.SurveyList.SurveyListResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import kotlinx.android.synthetic.main.activity_survey_list.*
import kotlinx.android.synthetic.main.activity_survey_list.emptyLayout
import kotlinx.android.synthetic.main.activity_survey_list.errorText
import kotlinx.android.synthetic.main.activity_survey_list.filter_spinner
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivitySurveyList : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var surveyListResponse: SurveyListResponse
    var sortingOptionsArraylist: ArrayList<String> = arrayListOf()
    var surveyArrayList: ArrayList<Data> = arrayListOf()
    private  val TAG = "SurveyList"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_list)
        initViews()
        clickEvents()
    }

    fun initViews() {
        sortingOptionsArraylist= arrayListOf(getString(R.string.newest),getString(R.string.oldest))
        initializeFilterSpinner()
        helperMethods = HelperMethods(this@ActivitySurveyList)
        preferencesHelper = SharedPreferencesHelper(this@ActivitySurveyList)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
        if (helperMethods.isConnectingToInternet) {
            surveyListApiCall(filter_spinner.getItemAtPosition(0).toString())
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun clickEvents() {
        navBack.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        surveyRecycler.setHasFixedSize(true)
        surveyRecycler.removeAllViews()
        surveyRecycler.layoutManager = LinearLayoutManager(this@ActivitySurveyList)
        surveyRecycler.adapter = SurveyListAdapter(this@ActivitySurveyList, this@ActivitySurveyList, surveyListResponse.data)

        if (surveyListResponse.data.size > 0) {
            emptyLayout.visibility = View.GONE
            surveyRecycler.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.there_is_no_testimonials_available)
            emptyLayout.visibility = View.VISIBLE
            surveyRecycler.visibility = View.GONE
        }
    }

    fun initializeFilterSpinner() {
        val typeface = ResourcesCompat.getFont(this@ActivitySurveyList, R.font.almarai_regular)
        val adapter = ArrayAdapter(this@ActivitySurveyList, R.layout.support_simple_spinner_dropdown_item, sortingOptionsArraylist)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        filter_spinner.adapter = adapter
        filter_spinner.setSelection(0, true)
        val v2 = filter_spinner.selectedView
        (v2 as TextView).textSize = 13f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@ActivitySurveyList, R.color.white))
        filter_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                surveyArrayList.clear()
                (view as TextView).textSize = 13f
                view.typeface = typeface
                view.setTextColor(ContextCompat.getColor(this@ActivitySurveyList, R.color.white))
                surveyListApiCall(sortingOptionsArraylist.get(filter_spinner.selectedItemPosition))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun surveyListApiCall(sortby:String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SURVEYS_API_CALL("Bearer ${dataUser.access_token}",sortby)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            surveyListResponse = Gson().fromJson(response.body()!!.string(), SurveyListResponse::class.java)
                            if (surveyListResponse.status == "200") {
                                InitializeRecyclerview()
                                surveyArrayList.addAll(surveyListResponse.data)

                            } else {
                                if (helperMethods.checkTokenValidation(surveyListResponse.status, surveyListResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), surveyListResponse.message)
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