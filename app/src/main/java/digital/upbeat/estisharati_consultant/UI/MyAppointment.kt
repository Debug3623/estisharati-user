package digital.upbeat.estisharati_consultant.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.Adapter.AppointmentAdapter
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.Appointment.Appointment
import digital.upbeat.estisharati_consultant.DataClassHelper.Appointment.AppointmentResponse
import digital.upbeat.estisharati_consultant.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.Utils.alertActionClickListner
import kotlinx.android.synthetic.main.activity_my_appointment.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyAppointment : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    var appointmentArrayList: ArrayList<Appointment> = arrayListOf()
    lateinit var appointmentsResponse: AppointmentResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_appointment)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            appointmentsApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@MyAppointment)
        preferencesHelper = SharedPreferencesHelper(this@MyAppointment)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInConsultant
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        appointmentRecycler.setHasFixedSize(true)
        appointmentRecycler.removeAllViews()
        appointmentRecycler.layoutManager = LinearLayoutManager(this@MyAppointment)
        appointmentRecycler.adapter = AppointmentAdapter(this@MyAppointment, this@MyAppointment, appointmentArrayList)

        if (appointmentArrayList.size > 0) {
            emptyLayout.visibility = View.GONE
            appointmentRecycler.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.you_did_not_purchase_any_course_till_now)
            emptyLayout.visibility = View.VISIBLE
            appointmentRecycler.visibility = View.GONE
        }
    }

    fun appointmentsApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.APPOINTMENTS_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            appointmentsResponse = Gson().fromJson(response.body()!!.string(), AppointmentResponse::class.java)
                            if (appointmentsResponse.status.equals("200")) {
                                appointmentArrayList = appointmentsResponse.data.appointments
                                InitializeRecyclerview()
                            } else {
                                if (helperMethods.checkTokenValidation(appointmentsResponse.status, appointmentsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert),appointmentsResponse.message)
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

    fun appointmentAction(position: Int, status: String, message: String) {
        if (status.equals("1")) {
            helperMethods.showAlertDialog(this@MyAppointment, object : alertActionClickListner {
                override fun onActionOk() {
                    if (helperMethods.isConnectingToInternet) {
                        appoveRejectApiCall(appointmentArrayList.get(position).id, status)
                    } else {
                        helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                    }
                }

                override fun onActionCancel() {
                }
            }, getString(R.string.appointment), message, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
        } else if (status.equals("2")) {
            helperMethods.showAlertDialog(this@MyAppointment, object : alertActionClickListner {
                override fun onActionOk() {
                    if (helperMethods.isConnectingToInternet) {
                        appoveRejectApiCall(appointmentArrayList.get(position).id, status)
                    } else {
                        helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                    }
                }

                override fun onActionCancel() {
                }
            }, getString(R.string.no_internet_connection), message, false, resources.getString(R.string.ok), resources.getString(R.string.cancel))
        }
    }

    fun findCategoryID(category_id: String): String {
        for (category in appointmentsResponse.data.categories) {
            if (category.id.equals(category_id)) {
                return category.name
            }
        }
        return ""
    }

    fun appoveRejectApiCall(appointmentId: String, status: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.APPOVE_REJECT_API_CALL("Bearer ${dataUser.access_token}", appointmentId, status)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            appointmentsResponse = Gson().fromJson(response.body()!!.string(), AppointmentResponse::class.java)
                            if (appointmentsResponse.status.equals("200")) {
                                helperMethods.showToastMessage(appointmentsResponse.message)
                                appointmentsApiCall()
                            } else {
                                if (helperMethods.checkTokenValidation(appointmentsResponse.status,appointmentsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), appointmentsResponse.message)
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