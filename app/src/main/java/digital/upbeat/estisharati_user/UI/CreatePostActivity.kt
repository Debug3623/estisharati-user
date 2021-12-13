package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_create_post.nav_back
import kotlinx.android.synthetic.main.activity_testimonials_details.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CreatePostActivity : AppCompatActivity() {
    lateinit var dataUser: DataUser
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        initViews()
        clickEvents()
    }
    fun initViews() {
        helperMethods = HelperMethods(this@CreatePostActivity)
        preferencesHelper = SharedPreferencesHelper(this@CreatePostActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser

    }
    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        postPublish.setOnClickListener {
            if (writePost.text.toString() == "") {
                helperMethods.showToastMessage(getString(R.string.please_feel_free_to_leave_your_posts))
                return@setOnClickListener
            }
            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            experiencCommentsApiCall(postPublish.text.toString())
        }
//        testimonialType.setOnClickListener {
//            if (testimonialsDetailsResponse.data.type.equals("consultant")) {
//                val intent = Intent(this@TestimonialsDetails, ConsultantDetails::class.java)
//                intent.putExtra("consultant_id", testimonialsDetailsResponse.data.consultant_id)
//                intent.putExtra("category_id",  testimonialsDetailsResponse.data.category_id)
//                startActivity(intent)
//            } else {
//                val intent = Intent(this@TestimonialsDetails, CourseDetails::class.java)
//                intent.putExtra("courseId", testimonialsDetailsResponse.data.course_id)
//                startActivity(intent)
//            }
//        }
    }

    private fun experiencCommentsApiCall(post: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.PUPLISH_POST_API_CALL("Bearer ${dataUser.access_token}", post)
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
                                startActivity(Intent(this@CreatePostActivity,PostsActivity::class.java))
                              //  experiencByIdApiCall()
                                helperMethods.hideSoftKeyboard(writePost)
                                writePost.text = "".toEditable()
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
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

}