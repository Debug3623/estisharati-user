package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.ConsultantCommentsReplyAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.CourseComments.CommentsResponse
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.CourseDetails
import kotlinx.android.synthetic.main.fragment_comments.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Comments(val courseDetails: CourseDetails) : Fragment() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        clickEvents()
        InitializeRecyclerview()
        setDetails()
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
    }

    fun setDetails() {
        avarageRatings.text = courseDetails.responseCoursesDetails.rate
        star5Percent.text = courseDetails.responseCoursesDetails.average_rating.five_star + " %"
        star4Percent.text = courseDetails.responseCoursesDetails.average_rating.four_star + " %"
        star3Percent.text = courseDetails.responseCoursesDetails.average_rating.three_star + " %"
        star2Percent.text = courseDetails.responseCoursesDetails.average_rating.two_star + " %"
        star1Percent.text = courseDetails.responseCoursesDetails.average_rating.one_star + " %"

        star5PercentView.setLayoutParams(getLayoutParams(courseDetails.responseCoursesDetails.average_rating.five_star))
        star4PercentView.setLayoutParams(getLayoutParams(courseDetails.responseCoursesDetails.average_rating.four_star))
        star3PercentView.setLayoutParams(getLayoutParams(courseDetails.responseCoursesDetails.average_rating.three_star))
        star2PercentView.setLayoutParams(getLayoutParams(courseDetails.responseCoursesDetails.average_rating.two_star))
        star1PercentView.setLayoutParams(getLayoutParams(courseDetails.responseCoursesDetails.average_rating.one_star))
    }

    fun InitializeRecyclerview() {
        course_comments_reply_recycler.setHasFixedSize(true)
        course_comments_reply_recycler.removeAllViews()
        course_comments_reply_recycler.layoutManager = LinearLayoutManager(requireContext())
        course_comments_reply_recycler.adapter = ConsultantCommentsReplyAdapter(requireContext(), this@Comments, null, courseDetails.responseCoursesDetails.comments, arrayListOf())
        if (courseDetails.responseCoursesDetails.comments.size > 0) {
            commentsHeader.visibility = View.VISIBLE
        } else {
            commentsHeader.visibility = View.GONE
        }
    }

    fun getLayoutParams(rating: String): LinearLayout.LayoutParams {
        val scale = context!!.resources.displayMetrics.density
        val pixels = (20 * scale + 0.5f)
        val param = LinearLayout.LayoutParams(0, pixels.toInt(), rating.toFloat())
        return param
    }

    fun courseCommentApiCall(courseId: String, parent_id: String, comment: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.COURSES_COMMENT_API_CALL("Bearer ${dataUser.access_token}", courseId, parent_id, comment)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val commentsResponse: CommentsResponse = Gson().fromJson(response.body()!!.string(), CommentsResponse::class.java)

                            if (commentsResponse.status.equals("200")) {
                                courseDetails.responseCoursesDetails.comments = commentsResponse.data
                                InitializeRecyclerview()
                                helperMethods.showToastMessage("Replied successfully !")
                            } else {
                                val message = JSONObject(response.body()!!.string()).getString("message")
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