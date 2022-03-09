package digital.upbeat.estisharati_user.UI

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.iosParameters
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Referral.ReferralResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_invite_app.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class InviteApp : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var referralResponse: ReferralResponse
    var mInvitationUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_app)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            referralApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@InviteApp)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@InviteApp)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        navBack.setOnClickListener { finish() }
    }

    fun setDetails() {
        discountPercentage.text = referralResponse.data.points + " %"
        referralCode.text = referralResponse.data.referral_code
        val invitationLink = "https://upbeat.digital/en?referral_code=${referralResponse.data.referral_code}"
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(invitationLink)
            domainUriPrefix = "https://estisharati.page.link"
            androidParameters("digital.upbeat.estisharati_user") {}
            iosParameters("com.upbeat.Estisharaty") {}
        }.addOnSuccessListener { shortDynamicLink ->
            mInvitationUrl = shortDynamicLink.shortLink.toString()+"?referral_code=${referralResponse.data.referral_code}"
            Log.d("mInvitationUrl",mInvitationUrl)
        }


        referralCode.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", referralResponse.data.referral_code)
            clipboard.setPrimaryClip(clip)
            helperMethods.showToastMessage(getString(R.string.copied_to_clipboard))
        }
        shareNow.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Please install the app by clicking link to get get amazing discounts in next purchase !. Referral Code "+referralResponse.data.referral_code+"     " + mInvitationUrl)
            sendIntent.setType("text/plain")
            startActivity(sendIntent)
        }
    }

    fun referralApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.REFERRAL_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            referralResponse = Gson().fromJson(response.body()!!.string(), ReferralResponse::class.java)
                            if (referralResponse.status.equals("200")) {
                                setDetails()
                            } else {
                                if (helperMethods.checkTokenValidation(referralResponse.status, referralResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.showToastMessage(referralResponse.message)
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