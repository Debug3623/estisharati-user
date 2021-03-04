package digital.upbeat.estisharati_user.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.TestimonialsCommentAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Testimonials.TestimonialsResponse
import digital.upbeat.estisharati_user.DataClassHelper.TestimonialsDetails.TestimonialsDetailsResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_testimonials_details.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class TestimonialsDetails : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    var testimonialId = ""
    lateinit var testimonialsDetailsResponse: TestimonialsDetailsResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testimonials_details)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            experiencByIdApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        testimonialId = intent.getStringExtra("testimonialId")!!
        helperMethods = HelperMethods(this@TestimonialsDetails)
        preferencesHelper = SharedPreferencesHelper(this@TestimonialsDetails)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        postComment.setOnClickListener {
            if (writeComment.text.toString().equals("")) {
                helperMethods.showToastMessage(getString(R.string.please_feel_free_to_leave_your_comments))
                return@setOnClickListener
            }
            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            experiencByIdApiCall(writeComment.text.toString())
        }
    }

    fun setTestimonialsDetails() {
        if (testimonialsDetailsResponse.data.type.equals("consultant")) {
            testimonialType.text = testimonialsDetailsResponse.data.service_name + " ( " + testimonialsDetailsResponse.data.consultant_category + " )"
        } else {
            testimonialType.text = testimonialsDetailsResponse.data.service_name
        }
        Glide.with(this@TestimonialsDetails).load(testimonialsDetailsResponse.data.user.image_path).apply(helperMethods.requestOption).into(profilePicture)
        userName.text = testimonialsDetailsResponse.data.user.name
        commentsCount.text = testimonialsDetailsResponse.data.comments_count + " " + getString(R.string.comments)
        testimonialsContent.text = testimonialsDetailsResponse.data.experience
        Glide.with(this@TestimonialsDetails).load(dataUser.image).apply(helperMethods.requestOption).into(myProfileImage)

        InitializeRecyclerview()
    }

    fun InitializeRecyclerview() {
        testimonialsCommentRecycler.setHasFixedSize(true)
        testimonialsCommentRecycler.removeAllViews()
        testimonialsCommentRecycler.layoutManager = LinearLayoutManager(this@TestimonialsDetails)
        testimonialsCommentRecycler.adapter = TestimonialsCommentAdapter(this@TestimonialsDetails, this@TestimonialsDetails, testimonialsDetailsResponse.data.comments)
    }

    fun experiencByIdApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GET_SHARE_EXPERIENCE_BY_ID_API_CALL("Bearer ${dataUser.access_token}", testimonialId)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            testimonialsDetailsResponse = Gson().fromJson(response.body()!!.string(), TestimonialsDetailsResponse::class.java)
                            if (testimonialsDetailsResponse.status.equals("200")) {
                                setTestimonialsDetails()
                            } else {
                                if (helperMethods.checkTokenValidation(testimonialsDetailsResponse.status, testimonialsDetailsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), testimonialsDetailsResponse.message)
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

    fun experiencByIdApiCall(comment: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SHARE_EXPERIENCE_COMMENTS_API_CALL("Bearer ${dataUser.access_token}", testimonialId, comment)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            val message = jsonObject.getString("message")

                            if (status.equals("200")) {
                                helperMethods.showToastMessage(message)
                                experiencByIdApiCall()
                            } else {
                                if (helperMethods.checkTokenValidation(status, message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), message)
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