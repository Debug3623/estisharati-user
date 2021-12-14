package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.MyCoursesAdapter
import digital.upbeat.estisharati_user.Adapter.TestimonialsAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.MyCourse.MyCourseResponse
import digital.upbeat.estisharati_user.DataClassHelper.Testimonials.TestimonialsResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_testimonials.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Testimonials : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser

    lateinit var testimonialsResponse: TestimonialsResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testimonials)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Testimonials)
        preferencesHelper = SharedPreferencesHelper(this@Testimonials)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }
    override fun onStart() {
        super.onStart()
        if (helperMethods.isConnectingToInternet) {
            experiencApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun InitializeRecyclerview() {

        testimonialsRecycler.setHasFixedSize(true)
        testimonialsRecycler.removeAllViews()
        testimonialsRecycler.layoutManager = LinearLayoutManager(this@Testimonials)
        testimonialsRecycler.adapter = TestimonialsAdapter(this@Testimonials, this@Testimonials, testimonialsResponse.data)

        if (testimonialsResponse.data.size > 0) {
            emptyLayout.visibility = View.GONE
            testimonialsRecycler.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.there_is_no_testimonials_available)
            emptyLayout.visibility = View.VISIBLE
            testimonialsRecycler.visibility = View.GONE
        }
    }

    fun experiencApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GET_SHARE_EXPERIENCE_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                             testimonialsResponse  = Gson().fromJson(response.body()!!.string(), TestimonialsResponse::class.java)
                            GlobalData.testimonialsResponse=testimonialsResponse
                            if (testimonialsResponse.status.equals("200")) {
                                InitializeRecyclerview()
                            } else {
                                if (helperMethods.checkTokenValidation(testimonialsResponse.status, testimonialsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), testimonialsResponse.message)
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