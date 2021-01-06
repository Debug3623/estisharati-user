package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.ConsultantCommentsReplyAdapter
import digital.upbeat.estisharati_user.Adapter.ConsultationsAdapter
import digital.upbeat.estisharati_user.Adapter.ConsultationsCategoryAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.ConsultantComments.commentsResponse
import digital.upbeat.estisharati_user.DataClassHelper.ConsultantDetails.ConsultantDetailsResponse
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.PackagesOptions.PackagesOptions
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_consultant_details.*
import kotlinx.android.synthetic.main.activity_consultant_details.favoriteIcon
import kotlinx.android.synthetic.main.activity_consultant_details.favoriteLayout
import kotlinx.android.synthetic.main.activity_consultant_details.nav_back
import kotlinx.android.synthetic.main.activity_consultant_details.offersEndDate
import kotlinx.android.synthetic.main.activity_consultant_details.offersEndDateLayout
import kotlinx.android.synthetic.main.activity_consultant_details.showMore
import kotlinx.android.synthetic.main.activity_course_details.*
import kotlinx.android.synthetic.main.consultation_category_layout.view.*
import kotlinx.android.synthetic.main.fragment_consultations.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ConsultantDetails : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var consultantDetailsResponse: ConsultantDetailsResponse
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_details)
        initViews()
        clickEvents()

        if (helperMethods.isConnectingToInternet) {
            consultantDetailsApiCall(intent.getStringExtra("consultant_id"))
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ConsultantDetails)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ConsultantDetails)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }

        req_consultation_now.setOnClickListener {
            redirectToPayment(intent.getStringExtra("category_id"))
        }
        favoriteLayout.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                addRemoveFavouriteConsultantApiCall(consultantDetailsResponse.id)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
    }

    fun setConsultantDetails() {
        actionBarTitle.text = consultantDetailsResponse.name
        Glide.with(this@ConsultantDetails).load(consultantDetailsResponse.image_path).apply(helperMethods.requestOption).into(consultantImage)
        Glide.with(this@ConsultantDetails).load(consultantDetailsResponse.image_path).apply(helperMethods.requestOption).into(backgroudImage)
        consultantName.text = consultantDetailsResponse.name
        consultantJobTitle.text = consultantDetailsResponse.job_title
        consultantRate.text = consultantDetailsResponse.rate
        consultantQualification.text = consultantDetailsResponse.qualification_brief
        if (helperMethods.findConsultantIsOnline(consultantDetailsResponse.id)) onlineStatus.visibility = View.VISIBLE else onlineStatus.visibility = View.GONE

        showMore.setOnClickListener {
            helperMethods.AlertPopup(getString(R.string.consultation_description), consultantDetailsResponse.qualification_brief)
        }
        if (consultantDetailsResponse.offerprice.equals("0")) {
            consultantPrice.text = "${getString(R.string.aed)} ${consultantDetailsResponse.consultant_cost}"
            consultantOldPrice.visibility = View.GONE
            offersEndDateLayout.visibility = View.GONE
        } else {
            consultantPrice.text = "${getString(R.string.aed)} ${consultantDetailsResponse.offerprice}"
            consultantOldPrice.text = "${getString(R.string.aed)} ${consultantDetailsResponse.consultant_cost}"
            offersEndDate.text = consultantDetailsResponse.offer_end
            offersEndDateLayout.visibility = View.VISIBLE

            consultantOldPrice.setPaintFlags(consultantOldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            consultantOldPrice.visibility = View.VISIBLE
        }
        if (consultantDetailsResponse.favourite) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_un_favorite)
        }
    }

    fun redirectToPayment(categoryId: String) {
        if (categoryId.equals("")) {
            showConsultationCategory()
        } else {
            val price = if (consultantDetailsResponse.offerprice.equals("0")) {
                consultantDetailsResponse.consultant_cost
            } else {
                consultantDetailsResponse.offerprice
            }
            val vatAmount: Float = price.toFloat() / 100.0f * 5
            val priceIncludedVat = vatAmount + price.toFloat()
            GlobalData.packagesOptions = PackagesOptions(consultantDetailsResponse.id, consultantDetailsResponse.name, "consultation", categoryId, price, vatAmount.toString(), priceIncludedVat.toString(), "", "", "")
            startActivity(Intent(this@ConsultantDetails, PackagesSelection::class.java))
        }
    }

    fun InitializeRecyclerview() {
        consultant_comments_reply_recycler.setHasFixedSize(true)
        consultant_comments_reply_recycler.removeAllViews()
        consultant_comments_reply_recycler.layoutManager = LinearLayoutManager(this@ConsultantDetails)
        consultant_comments_reply_recycler.adapter = ConsultantCommentsReplyAdapter(this@ConsultantDetails, null, this@ConsultantDetails, arrayListOf(), consultantDetailsResponse.comments)
        if (consultantDetailsResponse.comments.size > 0) {
            commentsHeader.visibility = View.VISIBLE
        } else {
            commentsHeader.visibility = View.GONE
        }
    }

    fun showConsultationCategory() {
        val popup_view = LayoutInflater.from(this@ConsultantDetails).inflate(R.layout.consultation_category_layout, null)
        val aleatdialog = AlertDialog.Builder(this@ConsultantDetails)

        aleatdialog.setView(popup_view)
        aleatdialog.setCancelable(false)
        dialog = aleatdialog.create()
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        popup_view.actionCancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        popup_view.consultationsRecycler.setHasFixedSize(true)
        popup_view.consultationsRecycler.removeAllViews()
        popup_view.consultationsRecycler.layoutManager = LinearLayoutManager(this@ConsultantDetails)
        popup_view.consultationsRecycler.adapter = ConsultationsCategoryAdapter(this@ConsultantDetails, this@ConsultantDetails, consultantDetailsResponse.categories)
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
                                setConsultantDetails()
                                InitializeRecyclerview()
                            } else {
                                val message = jsonObject.getString("message")
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
                                consultantDetailsResponse.comments = commentsResponse.data
                                InitializeRecyclerview()
                                helperMethods.showToastMessage(getString(R.string.replied_successfully))
                            } else {
                                val message = JSONObject(response.body()!!.string()).getString("message")
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

    fun addRemoveFavouriteConsultantApiCall(ConsultantID: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.FAVOURITE_CONSULTANT_API_CALL("Bearer ${dataUser.access_token}", ConsultantID)
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
                                val data = jsonObject.getString("data")
                                val favorite = JSONObject(data).getBoolean("favourite")
                                helperMethods.showToastMessage(message)
                                consultantDetailsResponse.favourite = favorite
                                if (consultantDetailsResponse.favourite) {
                                    favoriteIcon.setImageResource(R.drawable.ic_favorite)
                                } else {
                                    favoriteIcon.setImageResource(R.drawable.ic_un_favorite)
                                }
                            } else {
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
}