package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.MyConsultation.Data
import digital.upbeat.estisharati_user.DataClassHelper.MyConsultation.MyConsultationResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.appointment_layout.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ConsultantDateTime : AppCompatActivity() {

    lateinit var helperMethods: HelperMethods
    lateinit var myConsultation: Data
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    var myConsultationArrayList: ArrayList<Data> = arrayListOf()
    var consultantId = "0"
    var transaction = "0"

    var chat = "0"
    var audio = "0"
    var video = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_date_time)

        initViews()
        if (helperMethods.isConnectingToInternet) {
            myCoursesApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }

        consultantId= intent.getStringExtra("consultant_id").toString()
        transaction= intent.getStringExtra("transaction_amount").toString()
        video= intent.getStringExtra("video").toString()
        audio= intent.getStringExtra("audio").toString()
        chat= intent.getStringExtra("chat").toString()

    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantDateTime)
        preferencesHelper = SharedPreferencesHelper(this@ConsultantDateTime)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser

//        val myConsultationArrayList = intent.getParcelableArrayListExtra<Data>("myConsultationArrayList") as ArrayList<Data>
//        myConsultation = myConsultationArrayList.get(intent.getIntExtra("position", 0))
    }


    fun showAppointmentPopup() {
        val LayoutView = LayoutInflater.from(this@ConsultantDateTime).inflate(R.layout.subscription_date_layout, null)
        val aleatdialog = android.app.AlertDialog.Builder(this@ConsultantDateTime)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        LayoutView.actionOk.setOnClickListener {
            if (validateAppointment(LayoutView)) {
                saveAppointmentApiCall(myConsultationArrayList[0].consultant_id, LayoutView.appointmentData.text.toString(), LayoutView.appointmentTime.text.toString(), myConsultationArrayList[0].category_id)
////                dialog.dismiss()

            }
        }
        LayoutView.actionCancel.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@ConsultantDateTime, Packages::class.java)
            intent.putExtra("viaFrom", "Home")
            startActivity(intent)
        }
        LayoutView.appointmentLayout.setOnClickListener {
            helperMethods.ShowDateTimePicker(LayoutView.appointmentData, LayoutView.appointmentTime)
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
                                val chat_balance = dataObject.getString("chat_balance")
                                val audio_balance = dataObject.getString("audio_balance")
                                val video_balance = dataObject.getString("video_balance")

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
//                                val message = jsonObject.getString("message")
//                                helperMethods.showToastMessage(message)
                                //move to consultant  details screen
                                val intent = Intent(this@ConsultantDateTime, ConsultantDetails::class.java)
                                intent.putExtra("consultant_id", consultantId)
                                intent.putExtra("category_id", "")
                                if(video=="1"){
                                    intent.putExtra("video",video)
                                }else if(audio=="1"){
                                    intent.putExtra("audio",audio)
                                }else if(chat=="1"){
                                    intent.putExtra("chat",chat)
                                }
                                startActivity(intent)

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

    fun myCoursesApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.MY_CONSULTANTS_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val myConsultationResponse: MyConsultationResponse = Gson().fromJson(response.body()!!.string(), MyConsultationResponse::class.java)
                            if (myConsultationResponse.status.equals("200")) {
                                myConsultationArrayList = myConsultationResponse.data
                                showAppointmentPopup()
                            } else {
                                if (helperMethods.checkTokenValidation(myConsultationResponse.status, myConsultationResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), myConsultationResponse.message)
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