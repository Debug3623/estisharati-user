package digital.upbeat.estisharati_user.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.PostsAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Testimonials.TestimonialsResponse
import digital.upbeat.estisharati_user.DataClassHelper.posts.PostsResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_posts.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PostsActivity : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var postsResponse: PostsResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@PostsActivity)
        preferencesHelper = SharedPreferencesHelper(this@PostsActivity)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        btn_createPost.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))

        }
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
        postsRecycler.setHasFixedSize(true)
        postsRecycler.removeAllViews()
        postsRecycler.layoutManager = LinearLayoutManager(this@PostsActivity)
        postsRecycler.adapter = PostsAdapter(this@PostsActivity, this@PostsActivity, postsResponse.data)

        if (postsResponse.data.size > 0) {
            emptyLayout.visibility = View.GONE
            postsRecycler.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.there_is_no_testimonials_available)
            emptyLayout.visibility = View.VISIBLE
            postsRecycler.visibility = View.GONE
        }
    }

    fun experiencApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GET_POSTS_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            postsResponse = Gson().fromJson(response.body()!!.string(), PostsResponse::class.java)
                            GlobalData.postsResponse = postsResponse
                            if (postsResponse.status.equals("200")) {
                                InitializeRecyclerview()
                            } else {
                                if (helperMethods.checkTokenValidation(postsResponse.status, postsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), postsResponse.message)
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