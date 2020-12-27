package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.ConsultantComments.commentsResponse
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.MyConsultation.Data
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_consultation_details_video.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ConsultationDetailsVideo : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var myConsultation: Data
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultation_details_video)
        initViews()
        clickEvents()
        setConsultationDetails()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultationDetailsVideo)
        preferencesHelper = SharedPreferencesHelper(this@ConsultationDetailsVideo)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
        val myConsultationArrayList = intent.getParcelableArrayListExtra<Data>("myConsultationArrayList") as ArrayList<Data>
        myConsultation = myConsultationArrayList.get(intent.getIntExtra("position", 0))
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        consultantImage.setOnClickListener {
            val intent = Intent(this@ConsultationDetailsVideo, ConsultantDetails::class.java)
            intent.putExtra("consultant_id", myConsultation.consultant_id)
            startActivity(intent)
        }
    }

    fun setConsultationDetails() {
        consultantName.text = myConsultation.name
        consultantCategory.text = myConsultation.category_name
        Glide.with(this@ConsultationDetailsVideo).load(myConsultation.image_path).apply(helperMethods.requestOption).into(consultantImage)
        if (myConsultation.chat) {
            chatOption.visibility = View.VISIBLE
        } else {
            chatOption.visibility = View.GONE
        }
        if (myConsultation.audio) {
            voiceOption.visibility = View.VISIBLE
        } else {
            voiceOption.visibility = View.GONE
        }
        if (myConsultation.video) {
            videoOption.visibility = View.VISIBLE
        } else {
            videoOption.visibility = View.GONE
        }
        setUpPlayer()
    }

    fun setUpPlayer() {
        simpleExoPlayer = SimpleExoPlayer.Builder(this@ConsultationDetailsVideo).build()
        val uri = Uri.parse("https://assets.mixkit.co/videos/preview/mixkit-a-man-doing-jumping-tricks-at-the-beach-1222-large.mp4")
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        exoPlayer.setPlayer(simpleExoPlayer)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }

    override fun onStop() {
        simpleExoPlayer.stop()
        simpleExoPlayer.release()
        super.onStop()
    }


    fun showRatingPopup(consultantId: String) {
        val LayoutView = LayoutInflater.from(this@ConsultationDetailsVideo).inflate(R.layout.rating_popup, null)
        val aleatdialog = android.app.AlertDialog.Builder(this@ConsultationDetailsVideo)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val rating_bar = LayoutView.findViewById(R.id.rating_bar) as RatingBar
        val rating_based_cmd = LayoutView.findViewById(R.id.rating_based_cmd) as TextView
        val comments = LayoutView.findViewById(R.id.comments) as EditText
        val send = LayoutView.findViewById(R.id.send) as Button
        val mayBeLater = LayoutView.findViewById(R.id.mayBeLater) as TextView
        setRatingBasedCommend(rating_based_cmd, rating_bar.rating)

        rating_bar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            setRatingBasedCommend(rating_based_cmd, rating)
        }
        mayBeLater.setOnClickListener {
            dialog.dismiss()
        }
        send.setOnClickListener {
            if (comments.text.toString().equals("")) {
                helperMethods.showToastMessage("Please feel free to leave your comments")
                return@setOnClickListener
            }
            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            dialog.dismiss()

            mainCommentApiCall(consultantId, rating_bar.rating.toInt().toString(), comments.text.toString())
        }
    }

    fun setRatingBasedCommend(rating_based_cmd: TextView, rating: Float) {
        when (Math.round(rating)) {
            0 -> {
                rating_based_cmd.text = "Very bad"
            }
            1 -> {
                rating_based_cmd.text = "Bad"
            }
            2 -> {
                rating_based_cmd.text = "Average"
            }
            3 -> {
                rating_based_cmd.text = "Good"
            }
            4 -> {
                rating_based_cmd.text = "Very good"
            }
            5 -> {
                rating_based_cmd.text = "Very impressive"
            }
        }
    }

    fun mainCommentApiCall(consultantId: String, rate: String, comment: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.MAIN_CONSULTANT_COMMENT_API_CALL("Bearer ${dataUser.access_token}", consultantId, rate, comment)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val commentsResponse: commentsResponse = Gson().fromJson(response.body()!!.string(), commentsResponse::class.java)
                            if (commentsResponse.status.equals("200")) {
                                helperMethods.showToastMessage("Your rating and comments submitted successfully !")
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