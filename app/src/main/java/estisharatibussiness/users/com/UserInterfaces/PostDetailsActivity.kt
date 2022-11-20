package estisharatibussiness.users.com.UserInterfaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import estisharatibussiness.users.com.AdapterClasses.PostCommentAdapter
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.postDetails.PostDetailsResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import kotlinx.android.synthetic.main.activity_post_details.*
import kotlinx.android.synthetic.main.activity_post_details.nav_back
import kotlinx.android.synthetic.main.activity_post_details.postComment
import kotlinx.android.synthetic.main.activity_post_details.testimonialsCommentRecycler
import kotlinx.android.synthetic.main.activity_post_details.writeComment
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PostDetailsActivity : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    var postlId = ""
    lateinit var postDetailsResponse: PostDetailsResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            experiencByIdApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }
    fun initViews() {
        postlId = intent.getStringExtra("postId")!!
        helperMethods = HelperMethods(this@PostDetailsActivity)
        preferencesHelper = SharedPreferencesHelper(this@PostDetailsActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        postComment.setOnClickListener {
            if (writeComment.text.toString() == "") {
                helperMethods.showToastMessage(getString(R.string.please_feel_free_to_leave_your_comments))
                return@setOnClickListener
            }
            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            experiencCommentsApiCall(writeComment.text.toString())
        }
//        testimonialType.setOnClickListener {
//            if (testimonialsDetailsResponse.data.type.equals("consultant")) {
//                val intent = Intent(this@PostDetailsActivity, ConsultantDetails::class.java)
//                intent.putExtra("consultant_id", postDetailsResponse.data.consultant_id)
//                intent.putExtra("category_id",  postDetailsResponse.data.category_id)
//                startActivity(intent)
//            } else {
//                val intent = Intent(this@PostDetailsActivity, CourseDetails::class.java)
//                intent.putExtra("courseId", testimonialsDetailsResponse.data.course_id)
//                startActivity(intent)
//            }
//        }
    }

    fun setTestimonialsDetails() {
        Glide.with(this@PostDetailsActivity).load(postDetailsResponse.data.user.image).apply(helperMethods.requestOption).into(profilePicture)
        userName.text = postDetailsResponse.data.user.name
        commentsCount.text = postDetailsResponse.data.comments.size.toString() + " " + getString(R.string.comments)
        testimonialsContent.text = helperMethods.getHtmlText(postDetailsResponse.data.content)
        Glide.with(this@PostDetailsActivity).load(dataUser.image).apply(helperMethods.requestOption).into(myProfileImage)

        InitializeRecyclerview()
    }

    fun InitializeRecyclerview() {
        testimonialsCommentRecycler.setHasFixedSize(true)
        testimonialsCommentRecycler.removeAllViews()
        testimonialsCommentRecycler.layoutManager = LinearLayoutManager(this@PostDetailsActivity)
        testimonialsCommentRecycler.adapter = PostCommentAdapter(this@PostDetailsActivity, this, postDetailsResponse.data.comments)
    }
    fun experiencByIdApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GET_POST_BY_ID_API_CALL("Bearer ${dataUser.access_token}", postlId)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            postDetailsResponse = Gson().fromJson(response.body()!!.string(), PostDetailsResponse::class.java)
                            if (postDetailsResponse.status.equals("200")) {
                                setTestimonialsDetails()
                            } else {
                                if (helperMethods.checkTokenValidation(postDetailsResponse.status, postDetailsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), postDetailsResponse.message)
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
    fun experiencCommentsApiCall(comment: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SHARE_POST_COMMENTS_API_CALL("Bearer ${dataUser.access_token}", postlId, comment)
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
                                GlobalData.testimonialsResponse = null
                                helperMethods.hideSoftKeyboard(writeComment)
                                writeComment.text = "".toEditable()
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