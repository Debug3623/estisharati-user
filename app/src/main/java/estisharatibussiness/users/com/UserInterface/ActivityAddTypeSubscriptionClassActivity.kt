//package digital.upbeat.estisharati_user.UI
//
//import android.content.Intent
//import android.graphics.Color
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import com.google.gson.Gson
//import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
//import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
//import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
//import digital.upbeat.estisharati_user.DataClassHelper.Packages.Consultant
//import digital.upbeat.estisharati_user.DataClassHelper.Packages.Course
//import digital.upbeat.estisharati_user.DataClassHelper.Packages.Data
//import digital.upbeat.estisharati_user.DataClassHelper.Packages.PackagesResponse
//import digital.upbeat.estisharati_user.Helper.GlobalData
//import digital.upbeat.estisharati_user.Helper.HelperMethods
//import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
//import digital.upbeat.estisharati_user.R
//import kotlinx.android.synthetic.main.activity_add_type_subscription.*
//import okhttp3.ResponseBody
//import org.json.JSONException
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.IOException
//
//class AddTypeSubscriptionActivity : AppCompatActivity() {
//    lateinit var helperMethods: HelperMethods
//    lateinit var retrofitInterface: RetrofitInterface
//    lateinit var dataUser: DataUser
//    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
//    lateinit var packagesResponse: PackagesResponse
//    var voiceSelected = "true"
//    var videoSelected = ""
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_add_type_subscription)
//        val consultant = intent.getParcelableArrayListExtra<Consultant>("consultants") as ArrayList<Consultant>
//
//        //        initViews()
////        if (helperMethods.isConnectingToInternet) {
////            SUBSCRIPTIONS_API_CALL()
////        } else {
////            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
////        }
//
//        voice_layout_id.setOnClickListener {
//            voiceSelected ="true"
//            voice_layout_id.setBackgroundResource(R.drawable.corner_redius_30dp_transparent_white_and_white_stroke_radio)
//            chat_layout_id.setBackgroundResource(R.color.transparent)
//
//        }
//
//        chat_layout_id.setOnClickListener {
//            videoSelected= "true"
//            chat_layout_id.setBackgroundResource(R.drawable.corner_redius_30dp_transparent_white_and_white_stroke_radio)
//            voice_layout_id.setBackgroundResource(R.color.transparent)
//        }
//
//
//        if(voiceSelected.isEmpty()&&videoSelected.isEmpty())
//        {
//            Toast.makeText(this@AddTypeSubscriptionActivity, "Please select  at least one subscription type", Toast.LENGTH_SHORT).show()
//        }else{
//            choose_subscription_button.setOnClickListener {
//                val intent = Intent(this, ConsultantsInThePackage::class.java)
//                intent.putExtra("consultants", consultant)
//                startActivity(intent)
//            }
//        }
//
//
//
//
//
//    }
//
//}