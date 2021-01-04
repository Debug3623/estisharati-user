package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.ConsultantComments.commentsResponse
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.MyConsultation.Data
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_consultation_details_video.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ConsultationDetailsVideo : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var simpleExoPlayer: SimpleExoPlayer
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
            intent.putExtra("category_id", "")
            startActivity(intent)
        }
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
        simpleExoPlayer = SimpleExoPlayer.Builder(this@ConsultationDetailsVideo).build()
        val uri = Uri.parse(myConsultation.preview_video)
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        exoPlayer.setPlayer(simpleExoPlayer)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
    }

    override fun onStop() {
        simpleExoPlayer.stop()
        simpleExoPlayer.release()
        super.onStop()
    }
}