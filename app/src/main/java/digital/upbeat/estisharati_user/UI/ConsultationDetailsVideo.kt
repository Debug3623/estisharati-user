package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.MyConsultation.Data
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_consultation_details_video.*
import kotlinx.android.synthetic.main.appointment_layout.view.*
import kotlinx.android.synthetic.main.consultant_balance_popup.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ConsultationDetailsVideo : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    var simpleExoPlayer: SimpleExoPlayer? = null
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
            intent.putExtra("category_id", myConsultation.category_id)
            startActivity(intent)
        }
        chatPage.setOnClickListener {
            val intent = Intent(this@ConsultationDetailsVideo, ChatPage::class.java)
            intent.putExtra("user_id", myConsultation.consultant_id)
            intent.putExtra("forward_type", GlobalData.forwardType)
            intent.putExtra("forward_content", GlobalData.forwardContent)
            startActivity(intent)
            GlobalData.forwardType = ""
            GlobalData.forwardContent = ""
        }
        balanceBTN.setOnClickListener {
            getConsultationSecondsApiCall(myConsultation.consultant_id)
        }
        appointmentImage.setOnClickListener {
            showAppointmentPopup()
        }
    }

    fun showAppointmentPopup() {
        val LayoutView = LayoutInflater.from(this@ConsultationDetailsVideo).inflate(R.layout.appointment_layout, null)
        val aleatdialog = android.app.AlertDialog.Builder(this@ConsultationDetailsVideo)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        LayoutView.actionOk.setOnClickListener {
            if (validateAppointment(LayoutView)) {
                saveAppointmentApiCall(myConsultation.consultant_id, LayoutView.appointmentData.text.toString(), LayoutView.appointmentTime.text.toString(), myConsultation.category_id)
                dialog.dismiss()
            }
        }
        LayoutView.actionCancel.setOnClickListener {
            dialog.dismiss()
        }
        LayoutView.appointmentLayout.setOnClickListener {
            helperMethods.ShowDateTimePicker(LayoutView.appointmentData, LayoutView.appointmentTime)
        }
    }

    fun validateAppointment(LayoutView: View): Boolean {
        if (LayoutView.appointmentData.equals("")) {
            helperMethods.showToastMessage(getString(R.string.please_select_data_and_time))
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
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
        if (myConsultation.preview_video.equals("")) {
            exoPlayer.visibility = View.GONE
            placeHolder.visibility = View.VISIBLE
        } else {
            exoPlayer.visibility = View.VISIBLE
            placeHolder.visibility = View.GONE

            simpleExoPlayer = SimpleExoPlayer.Builder(this@ConsultationDetailsVideo).build()
            val uri = Uri.parse(myConsultation.preview_video)
            val mediaItem: MediaItem = MediaItem.fromUri(uri)
            exoPlayer.setPlayer(simpleExoPlayer)
            simpleExoPlayer?.setMediaItem(mediaItem)
            simpleExoPlayer?.prepare()
            simpleExoPlayer?.play()
        }
    }

    fun getConsultationSecondsApiCall(consultant_id: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GET_CONSULTATION_SECONDS_API_CALL("Bearer ${dataUser.access_token}", consultant_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val dataString = jsonObject.getString("data")
                                val dataObject = JSONObject(dataString)
                                val chat_balance = dataObject.getInt("chat_balance")
                                val audio_balance = dataObject.getInt("audio_balance")
                                val video_balance = dataObject.getInt("video_balance")
                                AlertPopup(chat_balance, audio_balance, video_balance)
                            } else {
                                val message = jsonObject.getString("message")
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

    fun saveAppointmentApiCall(consultant_id: String, data: String, time: String, categoryId: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SAVE_APPOINTMENT_API_CALL("Bearer ${dataUser.access_token}", consultant_id, data, time, categoryId)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                            } else {
                                val message = jsonObject.getString("message")
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

    fun AlertPopup(chat_balance: Int, audio_balance: Int, video_balance: Int) {
        val LayoutView = LayoutInflater.from(this@ConsultationDetailsVideo).inflate(R.layout.consultant_balance_popup, null)
        val aleatdialog = android.app.AlertDialog.Builder(this@ConsultationDetailsVideo)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        LayoutView.actionOkBtn.setOnClickListener {
            dialog.dismiss()
        }
        if (myConsultation.chat) {
            LayoutView.chatLayout.visibility = View.VISIBLE
        } else {
            LayoutView.chatLayout.visibility = View.GONE
        }
        if (myConsultation.audio) {
            LayoutView.voiceLayout.visibility = View.VISIBLE
        } else {
            LayoutView.voiceLayout.visibility = View.GONE
        }
        if (myConsultation.video) {
            LayoutView.videoLayout.visibility = View.VISIBLE
        } else {
            LayoutView.videoLayout.visibility = View.GONE
        }
        LayoutView.chatBalance.text = chat_balance.toString() + " " + getString(R.string.count)
        LayoutView.voiceBalance.text = (((audio_balance % 3600) / 60).toString() + "." + (audio_balance % 3600) % 60) + " " + getString(R.string.minutes)
        LayoutView.videoBalance.text = (((video_balance % 3600) / 60).toString() + "." + (video_balance % 3600) % 60) + " " + getString(R.string.minutes)
    }

    override fun onStop() {
        simpleExoPlayer?.let {
            it.stop()
        }
        super.onStop()
    }
}