package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.SubmitSurvey.Question
import digital.upbeat.estisharati_user.DataClassHelper.SubmitSurvey.SubmitSurvey
import digital.upbeat.estisharati_user.DataClassHelper.SurveysQuestions.SurveysQuestionsResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_survey.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Survey : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var surveysQuestionsResponse: SurveysQuestionsResponse
    var currentPosition = 0
    var survey_id = ""
    var mInvitationSurveryUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            surveysApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Survey)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@Survey)
        dataUser = sharedPreferencesHelper.logInUser
        survey_id = intent.getStringExtra("survey_id")!!
        val invitationLink = "https://www.estisharati.com?surveyId=$survey_id"
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(invitationLink)
            domainUriPrefix = "https://estisharati.page.link"
            androidParameters("digital.upbeat.estisharati_user") {}
            iosParameters("com.Estisharaty") {}
        }.addOnSuccessListener { shortDynamicLink ->
            mInvitationSurveryUrl = shortDynamicLink.shortLink.toString() + "?surveyId=$survey_id"
            Log.d("mInvitationUrl", mInvitationSurveryUrl)
        }
    }

    fun clickEvents() {
        navBack.setOnClickListener { finish() }
        startSurvey.setOnClickListener {
            if (surveysQuestionsResponse.data.questions.size > 0) {
                currentPosition = 0
                createRadioButton()
                checkPreviousNext()
            }
            surveyHostPage.visibility = View.GONE
            surveyQuestionPage.visibility = View.VISIBLE
        }
        previousQuestion.setOnClickListener {
            if (currentPosition != 0) {
                currentPosition--
                createRadioButton()
                checkPreviousNext()
            }
        }
        nextQuestion.setOnClickListener {
            if (currentPosition != surveysQuestionsResponse.data.questions.lastIndex) {
                if (surveysQuestionsResponse.data.questions.get(currentPosition).option_selected != null) {
                    currentPosition++
                    createRadioButton()
                    checkPreviousNext()
                } else {
                    helperMethods.showToastMessage(getString(R.string.please_choose_any_option))
                }
            }
        }
        submitSurvey.setOnClickListener {
            val questionArrayList: ArrayList<Question> = arrayListOf()
            for (question in surveysQuestionsResponse.data.questions) {
                if (question.option_selected != null) {
                    questionArrayList.add(Question(question.id, question.option_selected!!))
                } else {
                    helperMethods.showToastMessage(getString(R.string.please_choose_any_option))
                    return@setOnClickListener
                }
            }
            val submitSurvey: SubmitSurvey = SubmitSurvey(surveysQuestionsResponse.data.id, questionArrayList)
            Log.d("submitSurvey", Gson().toJson(submitSurvey))
            if (helperMethods.isConnectingToInternet) {
                surveysSubmitApiCall(submitSurvey)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        goToHome.setOnClickListener {
            val intent = Intent(this@Survey, UserDrawer::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        goToShare.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, "I have done this \" ${surveysQuestionsResponse.data.title} \"  survey  form ${getString(R.string.app_name)} you can try by clicking the link \n" + mInvitationSurveryUrl)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }

    fun checkPreviousNext() {
        if (currentPosition == 0) {
            previousQuestion.setColorFilter(ContextCompat.getColor(this@Survey, R.color.dark_gray))
        } else {
            previousQuestion.clearColorFilter()
        }
        if (currentPosition == surveysQuestionsResponse.data.questions.lastIndex) {
            nextQuestion.setColorFilter(ContextCompat.getColor(this@Survey, R.color.dark_gray))
        } else {
            nextQuestion.clearColorFilter()
        }
        if (currentPosition == surveysQuestionsResponse.data.questions.lastIndex) {
            submitSurvey.visibility = View.VISIBLE
        } else {
            submitSurvey.visibility = View.GONE
        }
    }

    fun setSurveyDetailsApi() {
        emptyLayout.visibility = View.GONE
        surveyHostPage.visibility = View.VISIBLE

        surveyTitle.text = surveysQuestionsResponse.data.title
        surveyDescription.text = helperMethods.getHtmlText(surveysQuestionsResponse.data.description)
        Glide.with(this@Survey).load(surveysQuestionsResponse.data.image_path).apply(helperMethods.requestOption).into(surveyImage)
    }

    fun createRadioButton() {
        currentPosition
        questionTxt.text = surveysQuestionsResponse.data.questions.get(currentPosition).question
        Glide.with(this@Survey).load(surveysQuestionsResponse.data.questions.get(currentPosition).image_path).apply(helperMethods.requestOption).into(questionImage)
        questionRadioGroup.removeAllViews()
        for (option in surveysQuestionsResponse.data.questions.get(currentPosition).options) {
            val rdbtn = RadioButton(this@Survey)
            rdbtn.id = option.id.toInt()
            rdbtn.isChecked = option.id.equals(surveysQuestionsResponse.data.questions.get(currentPosition).option_selected)
            rdbtn.setTextColor(ContextCompat.getColor(this@Survey, R.color.black))
            rdbtn.textSize = 16f
            val params = RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            if (option.image_path.equals("")) {
                rdbtn.text = option.name
                params.setMargins(0, 15, 0, 0)
            } else {
                params.setMargins(0, 25, 0, 0)
                rdbtn.text = option.name
                rdbtn.compoundDrawablePadding = 10
                Glide.with(this@Survey).load(option.image_path).apply(helperMethods.requestOption).into(object : CustomTarget<Drawable>(200, 150) {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        rdbtn.setCompoundDrawablesWithIntrinsicBounds(null, placeholder, null, null)
                    }

                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        rdbtn.setCompoundDrawablesWithIntrinsicBounds(null, resource, null, null)
                    }
                })
            }
            rdbtn.layoutParams = params


            questionRadioGroup.addView(rdbtn)
        }
        questionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            surveysQuestionsResponse.data.questions.get(currentPosition).option_selected = checkedId.toString()
        }
    }

    fun surveysApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SURVEY_DETAILS_API_CALL("Bearer ${dataUser.access_token}", survey_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            surveysQuestionsResponse = Gson().fromJson(response.body()!!.string(), SurveysQuestionsResponse::class.java)
                            if (surveysQuestionsResponse.status.equals("200")) {
                                setSurveyDetailsApi()
                            } else {
                                if (helperMethods.checkTokenValidation(surveysQuestionsResponse.status, surveysQuestionsResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.showToastMessage(surveysQuestionsResponse.message)
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

    fun surveysSubmitApiCall(submitSurvey: SubmitSurvey) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SURVEYS_SUBMIT_API_CALL("Bearer ${dataUser.access_token}", submitSurvey)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            val message = jsonObject.getString("message")
                            val data = jsonObject.getString("data")
                            if (status.equals("200")) {
                                completeSurvey.visibility = View.VISIBLE
                                surveyQuestionPage.visibility = View.GONE
                                messageSuccess.text = helperMethods.getHtmlText(data)
                                helperMethods.showToastMessage(message)
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
}