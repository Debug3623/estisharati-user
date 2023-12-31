package estisharatibussiness.users.com.UserInterfaces

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import estisharatibussiness.users.com.AdapterClasses.ConsultantCommentsReplyAdapter
import estisharatibussiness.users.com.AdapterClasses.ConsultationsCategoryAdapter
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantComments.commentsResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.ConsultantDetails.ConsultantDetailsResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.PackagesOptions.PackagesOptions
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_consultant_details.*
import kotlinx.android.synthetic.main.consultation_category_layout.view.*
import kotlinx.android.synthetic.main.consultation_category_layout.view.chatLayout
import kotlinx.android.synthetic.main.consultation_category_layout.view.videoLayout
import kotlinx.android.synthetic.main.consultation_category_layout.view.voiceLayout
import kotlinx.android.synthetic.main.preview_courses_popup.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivityConsultantDetails : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var consultantDetailsResponse: ConsultantDetailsResponse
    lateinit var dialog: AlertDialog
    var categoryId = ""
    lateinit var popup_view: View
    var chat = "0"
    var audio = "0"
    var video = "0"
    var chatr = "0"
    var audior = "0"
    var videor = "0"
    var price = 0.0
    var transaction = "0.0"
    var appointmentDate = "0"
    var appointmentTime = "0"
    var consultantId = ""
    var condition = ""
    var pricen = ""
    var meeting = "16 meetings-(60 min)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultant_details)
        initViews()
        clickEvents()

        if (helperMethods.isConnectingToInternet) {
            intent.getStringExtra("consultant_id")?.let { consultantDetailsApiCall(it) }
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }

    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityConsultantDetails)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivityConsultantDetails)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = sharedPreferencesHelper.logInUser

        videor= intent.getStringExtra("video").toString()
        audior= intent.getStringExtra("audio").toString()
        chatr= intent.getStringExtra("chat").toString()

        transaction= intent.getStringExtra("transaction_amount").toString()
        appointmentDate= intent.getStringExtra("appointment_date").toString()
        appointmentTime= intent.getStringExtra("appointment_time").toString()
        consultantId=intent.getStringExtra("consultant_id").toString()
        categoryId =intent.getStringExtra("category_id").toString()
        condition =intent.getStringExtra("condition").toString()
        pricen =intent.getStringExtra("price").toString()

      //  Received Data
        Log.d("video",videor)
        Log.d("audio",audior)
        Log.d("chat",chatr)
        Log.d("transaction",transaction)
        Log.d("appointmentDate",appointmentDate)
        Log.d("appointmentTime",appointmentTime)
        Log.d("consultant_id",consultantId)
        Log.d("category_id",categoryId)
        Log.d("condition",condition)

    }

    fun showConsultantPreviewPopup() {
        val layoutView = LayoutInflater.from(this@ActivityConsultantDetails).inflate(R.layout.preview_courses_popup, null)
        val aleatdialog = AlertDialog.Builder(this@ActivityConsultantDetails)
        aleatdialog.setView(layoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        layoutView.courseName.text = consultantDetailsResponse.name
        val simpleExoPlayer = SimpleExoPlayer.Builder(this@ActivityConsultantDetails).build()
        val uri = Uri.parse(consultantDetailsResponse.preview_video)
        Log.d("preview_video", uri.toString())
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        layoutView.exoPlayer.player = simpleExoPlayer
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
        layoutView.closePopup.setOnClickListener {
            simpleExoPlayer.stop()
            simpleExoPlayer.release()
            dialog.dismiss()
        }
    }

    fun clickEvents() {
        nav_back.setOnClickListener {
           if(condition=="1"){
               val intent = Intent(this@ActivityConsultantDetails, ActivityPackages::class.java)
               intent.putExtra("viaFrom", "Home")
               startActivity(intent)
               finish()
           }else{
               finish()
           }
        }

        req_consultation_now.setOnClickListener {
          if(condition == "1") {
              redirectToPayment()
          }else{
              showConsultationCategory()
          }

        }

        favoriteLayout.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                addRemoveFavouriteConsultantApiCall(consultantDetailsResponse.id)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        previewVideo.setOnClickListener { showConsultantPreviewPopup() }
        downloadDocument.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(consultantDetailsResponse.qualification))
            startActivity(intent)
        }
    }

    fun setConsultantPackageDetails() {
        actionBarTitle.text = consultantDetailsResponse.name
        Glide.with(this@ActivityConsultantDetails).load(consultantDetailsResponse.image_path).apply(helperMethods.requestOption).into(consultantImage)
        Glide.with(this@ActivityConsultantDetails).load(consultantDetailsResponse.image_path).apply(helperMethods.requestOption).into(backgroudImage)
        consultantName.text = consultantDetailsResponse.name
        consultantJobTitle.text = consultantDetailsResponse.job_title
        consultantRate.text = consultantDetailsResponse.rate
        consultantQualification.text = consultantDetailsResponse.qualification_brief
        if (helperMethods.findConsultantIsOnline(consultantDetailsResponse.id)) onlineStatus.visibility = View.VISIBLE else onlineStatus.visibility = View.GONE

        showMore.setOnClickListener {
            helperMethods.AlertPopup(getString(R.string.consultation_description), consultantDetailsResponse.qualification_brief)
        }

        if (consultantDetailsResponse.favourite) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_un_favorite)
        }

        if (consultantDetailsResponse.offer_end.equals("")) {
            offersEndDateLayout.visibility = View.GONE
        } else {
            offersEndDate.text = consultantDetailsResponse.offer_end
            offersEndDateLayout.visibility = View.VISIBLE
        }


        previewVideo.visibility = if (consultantDetailsResponse.preview_video != "") View.VISIBLE else View.GONE
        downloadDocument.visibility = if (consultantDetailsResponse.qualification != "") View.VISIBLE else View.GONE

     if (chatr == "1") {
         chatLayout.visibility = View.VISIBLE
         chatPriceLayout.visibility = View.VISIBLE
         if (consultantDetailsResponse.offer_chat_fee.equals("0")) {
             chatPrice.text = "${getString(R.string.usd)} ${pricen}"
             chatOldPrice.visibility = View.GONE
         } else {
             chatPrice.text = "${getString(R.string.usd)} ${pricen}"
             chatOldPrice.text = "${getString(R.string.usd)} ${pricen}"
             chatOldPrice.paintFlags = chatOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
             chatOldPrice.visibility = View.VISIBLE
         }
     } else {
         chatLayout.visibility = View.GONE
         chatPriceLayout.visibility = View.GONE
     }
     if (audior == "1") {
         voiceLayout.visibility = View.VISIBLE
         voicePriceLayout.visibility = View.VISIBLE
         if (consultantDetailsResponse.offer_voice_fee.equals("0")) {
             voicePrice.text = "${getString(R.string.usd)} ${pricen}"
             voiceOldPrice.visibility = View.GONE
         } else {
             voicePrice.text = "${getString(R.string.usd)} ${pricen}"
             voiceOldPrice.text = "${getString(R.string.usd)} ${pricen}"
             voiceOldPrice.paintFlags = voiceOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
             voiceOldPrice.visibility = View.VISIBLE
         }
     } else {
         voiceLayout.visibility = View.GONE
         voicePriceLayout.visibility = View.GONE
     }
     if (videor == "1") {
         videoLayout.visibility = View.VISIBLE
         videoPriceLayout.visibility = View.VISIBLE
         if (consultantDetailsResponse.offer_video_fee.equals("0")) {
             videoPrice.text = "${getString(R.string.usd)} ${pricen}"
             videoOldPrice.visibility = View.GONE
         } else {
             videoPrice.text = "${getString(R.string.usd)} ${pricen}"
             videoOldPrice.text = "${getString(R.string.usd)} ${pricen}"
             videoOldPrice.paintFlags = videoOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
             videoOldPrice.visibility = View.VISIBLE
         }
     } else {
         videoLayout.visibility = View.GONE
         videoPriceLayout.visibility = View.GONE
     }
            chatCount.text = meeting + " " + getString(R.string.written_chat)
            voiceCount.text = meeting
            videoCount.text = meeting

    }

    fun setConsultantDetails() {
        actionBarTitle.text = consultantDetailsResponse.name
        Glide.with(this@ActivityConsultantDetails).load(consultantDetailsResponse.image_path).apply(helperMethods.requestOption).into(consultantImage)
        Glide.with(this@ActivityConsultantDetails).load(consultantDetailsResponse.image_path).apply(helperMethods.requestOption).into(backgroudImage)
        consultantName.text = consultantDetailsResponse.name
        consultantJobTitle.text = consultantDetailsResponse.job_title
        consultantRate.text = consultantDetailsResponse.rate
        consultantQualification.text = consultantDetailsResponse.qualification_brief
        if (helperMethods.findConsultantIsOnline(consultantDetailsResponse.id)) onlineStatus.visibility = View.VISIBLE else onlineStatus.visibility = View.GONE

        showMore.setOnClickListener {
            helperMethods.AlertPopup(getString(R.string.consultation_description), consultantDetailsResponse.qualification_brief)
        }

        if (consultantDetailsResponse.favourite) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_un_favorite)
        }

        if (consultantDetailsResponse.offer_end.equals("")) {
            offersEndDateLayout.visibility = View.GONE
        } else {
            offersEndDate.text = consultantDetailsResponse.offer_end
            offersEndDateLayout.visibility = View.VISIBLE
        }


        previewVideo.visibility = if (consultantDetailsResponse.preview_video != "") View.VISIBLE else View.GONE
        downloadDocument.visibility = if (consultantDetailsResponse.qualification != "") View.VISIBLE else View.GONE

        if (consultantDetailsResponse.chat) {
            chatLayout.visibility = View.VISIBLE
            chatPriceLayout.visibility = View.VISIBLE
            if (consultantDetailsResponse.offer_chat_fee.equals("0")) {
                chatPrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.chat_fee}"
                chatOldPrice.visibility = View.GONE
            } else {
                chatPrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.offer_chat_fee}"
                chatOldPrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.chat_fee}"
                chatOldPrice.paintFlags = chatOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                chatOldPrice.visibility = View.VISIBLE
            }
        } else {
            chatLayout.visibility = View.GONE
            chatPriceLayout.visibility = View.GONE
        }
        if (consultantDetailsResponse.voice) {
            voiceLayout.visibility = View.VISIBLE
            voicePriceLayout.visibility = View.VISIBLE
            if (consultantDetailsResponse.offer_voice_fee.equals("0")) {
                voicePrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.voice_fee}"
                voiceOldPrice.visibility = View.GONE
            } else {
                voicePrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.offer_voice_fee}"
                voiceOldPrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.voice_fee}"
                voiceOldPrice.paintFlags = voiceOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                voiceOldPrice.visibility = View.VISIBLE
            }
        } else {
            voiceLayout.visibility = View.GONE
            voicePriceLayout.visibility = View.GONE
        }
        if (consultantDetailsResponse.video) {
            videoLayout.visibility = View.VISIBLE
            videoPriceLayout.visibility = View.VISIBLE
            if (consultantDetailsResponse.offer_video_fee.equals("0")) {
                videoPrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.video_fee}"
                videoOldPrice.visibility = View.GONE
            } else {
                videoPrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.offer_video_fee}"
                videoOldPrice.text = "${getString(R.string.usd)} ${consultantDetailsResponse.video_fee}"
                videoOldPrice.paintFlags = videoOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                videoOldPrice.visibility = View.VISIBLE
            }
        } else {
            videoLayout.visibility = View.GONE
            videoPriceLayout.visibility = View.GONE
        }

        chatCount.text = consultantDetailsResponse.chat_count + " " + getString(R.string.written_chat)
        voiceCount.text = helperMethods.formatToSecond(consultantDetailsResponse.voice_count)
        videoCount.text = helperMethods.formatToSecond(consultantDetailsResponse.video_count)
    }

    fun redirectToPayment() {

        if(condition == "1") {
            val intent = Intent(this@ActivityConsultantDetails, ActivityPackagesSelection::class.java)
            intent.putExtra("appointment_date", appointmentDate)
            intent.putExtra("appointment_time", appointmentTime)
            intent.putExtra("consultant_id", consultantId)
            intent.putExtra("category_id", categoryId)
            intent.putExtra("condition", "1")
            startActivity(intent)
        }else{
            GlobalData.packagesOptions = PackagesOptions(consultantDetailsResponse.id, consultantDetailsResponse.name, "consultation", categoryId, chat, audio, video, price.toString(), "0", "0", "", "", "0", "0", "", "0", "0")
            startActivity(Intent(this@ActivityConsultantDetails, ActivityPackagesSelection::class.java))
            intent.putExtra("condition", "0")
        }

    }

    fun InitializeRecyclerview() {
        consultant_comments_reply_recycler.setHasFixedSize(true)
        consultant_comments_reply_recycler.removeAllViews()
        consultant_comments_reply_recycler.layoutManager = LinearLayoutManager(this@ActivityConsultantDetails)
        consultant_comments_reply_recycler.adapter = ConsultantCommentsReplyAdapter(this@ActivityConsultantDetails, null, this@ActivityConsultantDetails, arrayListOf(), consultantDetailsResponse.comments)
        if (consultantDetailsResponse.comments.size > 0) {
            commentsHeader.visibility = View.VISIBLE
        } else {
            commentsHeader.visibility = View.GONE
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

                                if(condition=="1"){
                                    setConsultantPackageDetails()
                                }else{
                                    setConsultantDetails()
                                }

                                InitializeRecyclerview()
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

    fun showConsultationCategory() {
        popup_view = LayoutInflater.from(this@ActivityConsultantDetails).inflate(R.layout.consultation_category_layout, null)
        val aleatdialog = AlertDialog.Builder(this@ActivityConsultantDetails)
        aleatdialog.setView(popup_view)
        aleatdialog.setCancelable(false)
        dialog = aleatdialog.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        popup_view.actionCancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        popup_view.consultationsRecycler.setHasFixedSize(true)
        popup_view.consultationsRecycler.removeAllViews()
        popup_view.consultationsRecycler.layoutManager = LinearLayoutManager(this@ActivityConsultantDetails)
        popup_view.consultationsRecycler.adapter = ConsultationsCategoryAdapter(this@ActivityConsultantDetails, this@ActivityConsultantDetails, consultantDetailsResponse.categories)
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
            popup_view.chatText.setTextColor(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.black))
            if (consultantDetailsResponse.offer_chat_fee.equals("0")) {
                price += consultantDetailsResponse.chat_fee.toDouble()
            } else {
                price += consultantDetailsResponse.offer_chat_fee.toDouble()
            }
        } else {
            popup_view.chatImage.setColorFilter(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.gray))
            popup_view.chatText.setTextColor(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.gray))
        }
        if (audio.equals("1")) {
            popup_view.voiceImage.clearColorFilter()
            popup_view.voiceText.setTextColor(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.black))
            if (consultantDetailsResponse.offer_voice_fee.equals("0")) {
                price += consultantDetailsResponse.voice_fee.toDouble()
            } else {
                price += consultantDetailsResponse.offer_voice_fee.toDouble()
            }
        } else {
            popup_view.voiceImage.setColorFilter(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.gray))
            popup_view.voiceText.setTextColor(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.gray))
        }
        if (video.equals("1")) {
            popup_view.videoImage.clearColorFilter()
            popup_view.videoText.setTextColor(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.black))
            if (consultantDetailsResponse.offer_voice_fee.equals("0")) {
                price += consultantDetailsResponse.video_fee.toDouble()
            } else {
                price += consultantDetailsResponse.offer_video_fee.toDouble()
            }
        } else {
            popup_view.videoImage.setColorFilter(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.gray))
            popup_view.videoText.setTextColor(ContextCompat.getColor(this@ActivityConsultantDetails, R.color.gray))
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
                redirectToPayment()
            } else {
                helperMethods.showToastMessage(getString(R.string.please_select_your_consultation_type))
            }
        }
    }


}