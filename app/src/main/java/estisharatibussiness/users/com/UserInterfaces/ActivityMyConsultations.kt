package estisharatibussiness.users.com.UserInterfaces

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import estisharatibussiness.users.com.AdapterClasses.MyConsultationsAdapter
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantComments.commentsResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.MyConsultation.Data
import estisharatibussiness.users.com.DataClassHelperMehtods.MyConsultation.MyConsultationResponse
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_my_consultations.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivityMyConsultations : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    var myConsultationArrayList: ArrayList<Data> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_consultations)

        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            myCoursesApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityMyConsultations)
        preferencesHelper = SharedPreferencesHelper(this@ActivityMyConsultations)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        myConsultationsRecycler.setHasFixedSize(true)
        myConsultationsRecycler.removeAllViews()
        myConsultationsRecycler.layoutManager = LinearLayoutManager(this@ActivityMyConsultations)
        myConsultationsRecycler.adapter = MyConsultationsAdapter(this@ActivityMyConsultations, this@ActivityMyConsultations, myConsultationArrayList)
        if (myConsultationArrayList.size > 0) {
            emptyLayout.visibility = View.GONE
            myConsultationsRecycler.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.you_did_not_purchase_any_consultation_till_now)
            emptyLayout.visibility = View.VISIBLE
            myConsultationsRecycler.visibility = View.GONE
        }
    }

    fun showRatingPopup(ConsultationItem: Data) {
        val LayoutView = LayoutInflater.from(this@ActivityMyConsultations).inflate(R.layout.rating_popup, null)
        val aleatdialog = android.app.AlertDialog.Builder(this@ActivityMyConsultations)
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
        val title = LayoutView.findViewById(R.id.title) as TextView
        setRatingBasedCommend(rating_based_cmd, rating_bar.rating)
        title.text = ConsultationItem.name
        rating_bar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            setRatingBasedCommend(rating_based_cmd, rating)
        }
        mayBeLater.setOnClickListener {
            dialog.dismiss()
        }
        send.setOnClickListener {
            if (comments.text.toString().equals("")) {
                helperMethods.showToastMessage(getString(R.string.please_feel_free_to_leave_your_comments))
                return@setOnClickListener
            }
            if (!helperMethods.isConnectingToInternet) {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
                return@setOnClickListener
            }
            dialog.dismiss()

            mainCommentApiCall(ConsultationItem.consultant_id, ConsultationItem.category_id, rating_bar.rating.toInt().toString(), comments.text.toString())
        }
    }

    fun setRatingBasedCommend(rating_based_cmd: TextView, rating: Float) {
        when (Math.round(rating)) {
            0 -> {
                rating_based_cmd.text = getString(R.string.very_bad)
            }
            1 -> {
                rating_based_cmd.text = getString(R.string.bad)
            }
            2 -> {
                rating_based_cmd.text = getString(R.string.average)
            }
            3 -> {
                rating_based_cmd.text = getString(R.string.good)
            }
            4 -> {
                rating_based_cmd.text = getString(R.string.very_good)
            }
            5 -> {
                rating_based_cmd.text = getString(R.string.very_impressive)
            }
        }
    }

    fun mainCommentApiCall(consultantId: String, category_id: String, rate: String, comment: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.MAIN_CONSULTANT_COMMENT_API_CALL("Bearer ${dataUser.access_token}", consultantId, category_id, rate, comment)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val commentsResponse: commentsResponse = Gson().fromJson(response.body()!!.string(), commentsResponse::class.java)
                            if (commentsResponse.status.equals("200")) {
                                helperMethods.showToastMessage(getString(R.string.your_rating_and_comments_submitted_successfully))
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
                                InitializeRecyclerview()
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
