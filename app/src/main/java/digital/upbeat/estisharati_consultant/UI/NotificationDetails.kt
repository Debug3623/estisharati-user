package digital.upbeat.estisharati_consultant.UI

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.Adapter.NotificationsSubAdapter
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.ConsultantComments.commentsResponse
import digital.upbeat.estisharati_consultant.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_consultant.DataClassHelper.Notification.Data
import digital.upbeat.estisharati_consultant.DataClassHelper.Notification.Detail
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_notification_details.*
import kotlinx.android.synthetic.main.activity_notifications.nav_back
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class NotificationDetails : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var notificationsItem: Data
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var notificationsItemDetails: Detail
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_details)
        initViews()
        clickEvents()
        setNotificationDetails()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@NotificationDetails)
        sharedPreferencesHelper = SharedPreferencesHelper(this@NotificationDetails)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = sharedPreferencesHelper.logInConsultant
        notificationsItem = GlobalData.notificationResponse.data.get(intent.getIntExtra("position", 0))
        notificationsItemDetails = notificationsItem.details!!.get(0)
    }

    fun setNotificationDetails() {
        notificationTitle.text = notificationsItem.title
        notificationMessage.text = notificationsItem.body
        notificationDate.text = notificationsItem.created_at

        Glide.with(this@NotificationDetails).load(dataUser.image).apply(helperMethods.requestOption).into(myProfileImage)
        Glide.with(this@NotificationDetails).load(notificationsItemDetails.user.image_path).apply(helperMethods.requestOption).into(userImage)
        userNmae.text = notificationsItemDetails.user.name
        userRating.rating = notificationsItemDetails.review.toDouble().toInt().toFloat()
        reply.text = "" + notificationsItemDetails.replies.size + " "+getString(R.string.reply)
        commenteTime.text = notificationsItemDetails.created_at
        message.text = notificationsItemDetails.comment

        if (notificationsItem.details != null) {
            notificationsItem.details?.let {
                if (it.get(0).type.equals("course")) {
                    notificationImg.setImageResource(R.drawable.ic_courses_round)
                } else if (it.get(0).type.equals("consultant")) {
                    notificationImg.setImageResource(R.drawable.ic_consultant_round)
                } else {
                    notificationImg.setImageResource(R.mipmap.ic_launcher)
                }
            }
        } else {
            notificationImg.setImageResource(R.mipmap.ic_launcher)
        }

        InitializeRecyclerview()
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        main_item.setOnClickListener {
            sub_item_reply_layout.visibility = if (sub_item_reply_layout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        submitReply.setOnClickListener {
            if (!comments.text.toString().equals("")) {
                if (helperMethods.isConnectingToInternet) {
                    if (notificationsItemDetails.type.equals("course")) {
                        courseCommentApiCall(notificationsItemDetails.course_id, notificationsItemDetails.id, comments.text.toString())
                    } else if (notificationsItemDetails.type.equals("consultant")) {
                        consultantCommentApiCall(notificationsItemDetails.consultant_id, notificationsItemDetails.id, comments.text.toString())
                    }
                } else {
                    helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                }
            } else {
                helperMethods.showToastMessage(getString(R.string.please_feel_free_to_leave_your_comments))
            }
        }
    }

    fun InitializeRecyclerview() {
        notification_sub_recycler.setHasFixedSize(true)
        notification_sub_recycler.removeAllViews()
        notification_sub_recycler.layoutManager = LinearLayoutManager(this@NotificationDetails)
        notification_sub_recycler.adapter = NotificationsSubAdapter(this@NotificationDetails, this@NotificationDetails, notificationsItemDetails.replies)
    }

    fun consultantCommentApiCall(consultant_id: String, parent_id: String, comment: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.CONSULTANT_COMMENT_API_CALL("Bearer ${dataUser.access_token}", consultant_id, parent_id, comment)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val commentsResponse: commentsResponse = Gson().fromJson(response.body()!!.string(), commentsResponse::class.java)

                            if (commentsResponse.status.equals("200")) {
                                helperMethods.showToastMessage(commentsResponse.message)
                                finish()
                            } else {
                                if (helperMethods.checkTokenValidation(commentsResponse.status, commentsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), commentsResponse.message)
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

    fun courseCommentApiCall(courseId: String, parent_id: String, comment: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.COURSES_COMMENT_API_CALL("Bearer ${dataUser.access_token}", courseId, parent_id, comment)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val commentsResponse: commentsResponse = Gson().fromJson(response.body()!!.string(), commentsResponse::class.java)

                            if (commentsResponse.status.equals("200")) {
                                helperMethods.showToastMessage(commentsResponse.message)
                                finish()
                            } else {
                                if (helperMethods.checkTokenValidation(commentsResponse.status, commentsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert),commentsResponse.message)
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