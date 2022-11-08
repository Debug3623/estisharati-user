package digital.upbeat.estisharati_user.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.ConsultantCommentsReplyAdapter
import digital.upbeat.estisharati_user.Adapter.ConsultationsCategoryAdapter
import digital.upbeat.estisharati_user.Adapter.ConsultationsScheduleAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.ConsultantComments.commentsResponse
import digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails.ConsultantDetailsResponse
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.MyConsultation.Data
import digital.upbeat.estisharati_user.DataClassHelper.MyConsultation.MyConsultationResponse
import digital.upbeat.estisharati_user.DataClassHelper.PackagesOptions.PackagesOptions
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_consultant_categories.*
import kotlinx.android.synthetic.main.activity_consultant_details.*
import kotlinx.android.synthetic.main.activity_packages.*
import kotlinx.android.synthetic.main.appointment_layout.view.*
import kotlinx.android.synthetic.main.consultation_category_layout.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ConsultantCategories : AppCompatActivity() {

    lateinit var helperMethods: HelperMethods
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var myConsultation: Data
    lateinit var consultantDetailsResponse: ConsultantDetailsResponse
    var myConsultationArrayList: ArrayList<Data> = arrayListOf()
    lateinit var dialog: AlertDialog
    var categoryId = ""
    lateinit var popup_view: View
    var chat = "0"
    var audio = "0"
    var video = "0"
    var price = 0.0
    var consultantId = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_categories)

        initViews()

        if (helperMethods.isConnectingToInternet) {
            intent.getStringExtra("consultant_id")?.let { consultantDetailsApiCall(it) }
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }




    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantCategories)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ConsultantCategories)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = sharedPreferencesHelper.logInUser

        intent.getStringExtra("category_id")?.let { categoryId = it }
        consultantId= intent.getStringExtra("consultant_id").toString()
    }


    fun showConsultationCategory() {
        popup_view = LayoutInflater.from(this@ConsultantCategories).inflate(R.layout.consultation_category_layout, null)
        val aleatdialog = AlertDialog.Builder(this@ConsultantCategories)
        aleatdialog.setView(popup_view)
        aleatdialog.setCancelable(false)
        dialog = aleatdialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        popup_view.actionCancelBtn.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@ConsultantCategories, Packages::class.java)
            intent.putExtra("viaFrom", "Home")
            startActivity(intent)
            finish()
        }
        popup_view.consultationsRecycler.setHasFixedSize(true)
        popup_view.consultationsRecycler.removeAllViews()
        popup_view.consultationsRecycler.layoutManager = LinearLayoutManager(this@ConsultantCategories)
        popup_view.consultationsRecycler.adapter = ConsultationsScheduleAdapter(this@ConsultantCategories, this@ConsultantCategories, consultantDetailsResponse.categories)
        if (categoryId.equals("")) {
            popup_view.consultationsOptionLayout.visibility = View.GONE
            popup_view.consultationPriceLayout.visibility = View.GONE
            popup_view.actionProceedBtn.visibility = View.GONE
            popup_view.consultationsRecycler.visibility = View.VISIBLE
        } else {
            showConsultationPriceDetailsPopup()
        }
    }

    fun showConsultationPriceDetailsPopup() {
        price = 0.0
        popup_view.consultationsOptionLayout.visibility = View.VISIBLE
        popup_view.consultationPriceLayout.visibility = View.VISIBLE
        popup_view.actionProceedBtn.visibility = View.VISIBLE
        popup_view.consultationsRecycler.visibility = View.GONE
        popup_view.chatLayout.visibility = if (consultantDetailsResponse.chat) View.VISIBLE else View.GONE
        popup_view.voiceLayout.visibility = if (consultantDetailsResponse.voice) View.VISIBLE else View.GONE
        popup_view.videoLayout.visibility = if (consultantDetailsResponse.video) View.VISIBLE else View.GONE

        popup_view.chatCount.text = consultantDetailsResponse.chat_count + " " + getString(R.string.written_chat)
        popup_view.voiceCount.text = helperMethods.formatToSecond(consultantDetailsResponse.voice_count)
        popup_view.videoCount.text = helperMethods.formatToSecond(consultantDetailsResponse.video_count)
        if (chat.equals("1")) {
            popup_view.chatImage.clearColorFilter()
            popup_view.chatText.setTextColor(ContextCompat.getColor(this@ConsultantCategories, R.color.black))
            if (consultantDetailsResponse.offer_chat_fee.equals("0")) {
                price += consultantDetailsResponse.chat_fee.toDouble()
            } else {
                price += consultantDetailsResponse.offer_chat_fee.toDouble()
            }
        } else {
            popup_view.chatImage.setColorFilter(ContextCompat.getColor(this@ConsultantCategories, R.color.gray))
            popup_view.chatText.setTextColor(ContextCompat.getColor(this@ConsultantCategories, R.color.gray))
        }
        if (audio.equals("1")) {
            popup_view.voiceImage.clearColorFilter()
            popup_view.voiceText.setTextColor(ContextCompat.getColor(this@ConsultantCategories, R.color.black))
            if (consultantDetailsResponse.offer_voice_fee.equals("0")) {
                price += consultantDetailsResponse.voice_fee.toDouble()
            } else {
                price += consultantDetailsResponse.offer_voice_fee.toDouble()
            }
        } else {
            popup_view.voiceImage.setColorFilter(ContextCompat.getColor(this@ConsultantCategories, R.color.gray))
            popup_view.voiceText.setTextColor(ContextCompat.getColor(this@ConsultantCategories, R.color.gray))
        }
        if (video.equals("1")) {
            popup_view.videoImage.clearColorFilter()
            popup_view.videoText.setTextColor(ContextCompat.getColor(this@ConsultantCategories, R.color.black))
            if (consultantDetailsResponse.offer_voice_fee.equals("0")) {
                price += consultantDetailsResponse.video_fee.toDouble()
            } else {
                price += consultantDetailsResponse.offer_video_fee.toDouble()
            }
        } else {
            popup_view.videoImage.setColorFilter(ContextCompat.getColor(this@ConsultantCategories, R.color.gray))
            popup_view.videoText.setTextColor(ContextCompat.getColor(this@ConsultantCategories, R.color.gray))
        }
        popup_view.chatLayout.setOnClickListener {
            if (chat.equals("1")) {
                chat = "0"
            } else {
                chat = "1"
                audio = "0"
                video = "0"
            }
            showConsultationPriceDetailsPopup()
        }
        popup_view.voiceLayout.setOnClickListener {
            if (audio.equals("1")) {
                audio = "0"
            } else {
                audio = "1"
                chat = "0"
                video = "0"
            }
            showConsultationPriceDetailsPopup()
        }
        popup_view.videoLayout.setOnClickListener {
            if (video.equals("1")) {
                video = "0"
            } else {
                video = "1"
                audio = "0"
                chat = "0"
            }
            showConsultationPriceDetailsPopup()
        }
        popup_view.consultantPrice.text = "${getString(R.string.usd)} ${helperMethods.convetDecimalFormat(price)}"

        popup_view.actionProceedBtn.setOnClickListener {
            if (chat.equals("1") || audio.equals("1") || video.equals("1")) {

                dialog.dismiss()

                GlobalData.packagesOptions = PackagesOptions(consultantDetailsResponse.id, consultantDetailsResponse.name, "consultation", categoryId, chat, audio, video, price.toString(), "0", "0", "", "", "0", "0", "", "0", "0")

                showAppointmentPopup()

            } else {
                helperMethods.showToastMessage(getString(R.string.please_select_your_consultation_type))
            }
        }
    }

    fun consultantDetailsApiCall(consultant_id: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.CONSULTANT_API_CALL("Bearer ${dataUser.access_token}", consultant_id)
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
                                consultantDetailsResponse = Gson().fromJson(dataString, ConsultantDetailsResponse::class.java)
                                showConsultationCategory()
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


    fun showAppointmentPopup() {
        val LayoutView = LayoutInflater.from(this@ConsultantCategories).inflate(R.layout.subscription_date_layout, null)
        val aleatdialog = android.app.AlertDialog.Builder(this@ConsultantCategories)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        LayoutView.actionOk.setOnClickListener {
            if (validateAppointment(LayoutView)) {
//               saveAppointmentApiCall(myConsultation.consultant_id, LayoutView.appointmentData.text.toString(), LayoutView.appointmentTime.text.toString(), myConsultation.category_id)
                val intent = Intent(this@ConsultantCategories, ConsultantDetails::class.java)
                intent.putExtra("consultant_id", consultantId)
                intent.putExtra("category_id", categoryId)
                intent.putExtra("appointment_date", LayoutView.appointmentData.text.toString())
                intent.putExtra("appointment_time", LayoutView.appointmentTime.text.toString())
                if(video=="1"){
                    intent.putExtra("video",video)
                }else if(audio=="1"){
                    intent.putExtra("audio",audio)
                }else if(chat=="1"){
                    intent.putExtra("chat",chat)
                }
                startActivity(intent)
            }
        }
        LayoutView.actionCancel.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@ConsultantCategories, Packages::class.java)
            intent.putExtra("viaFrom", "Home")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
            this.finish()
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





}