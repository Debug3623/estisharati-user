package digital.upbeat.estisharati_user.UI

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_video_full_screen.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class VideoFullScreen : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var simpleExoPlayer: SimpleExoPlayer
    var handler: Handler = Handler()
    var runnable: Runnable = object : Runnable {
        override fun run() {
            GlobalData.lessonsPlayingDuration = simpleExoPlayer.currentPosition
            val percentage = (GlobalData.lessonsPlayingDuration * 100) / simpleExoPlayer.duration
            Log.d("playerChange", "    " + GlobalData.lessonsPlayingDuration + "    " + percentage)
            if (percentage > 75) {
                if (!GlobalData.lessonArrayList.get(GlobalData.lessonsPlayingPosition).watched) {
                    GlobalData.lessonArrayList.get(GlobalData.lessonsPlayingPosition).watched = true
                    lessonCompletedApiCall(GlobalData.lessonArrayList.get(GlobalData.lessonsPlayingPosition).course_id, GlobalData.lessonArrayList.get(GlobalData.lessonsPlayingPosition).chapter_id, GlobalData.lessonArrayList.get(GlobalData.lessonsPlayingPosition).id)
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_full_screen)
        initViews()
        clickEvents()
        setUpPlayer()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@VideoFullScreen)
        preferencesHelper = SharedPreferencesHelper(this@VideoFullScreen)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        simpleExoPlayer = SimpleExoPlayer.Builder(this@VideoFullScreen).build()
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener {
            finish()
        }
    }

    fun setUpPlayer() {
        handler.postDelayed(runnable, 1000)
        exoPlayer.setPlayer(simpleExoPlayer)
        simpleExoPlayer.setMediaItems(GlobalData.mediaItemArrayList)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
        simpleExoPlayer.seekTo(GlobalData.lessonsPlayingPosition, GlobalData.lessonsPlayingDuration)
        playerTitle.text = GlobalData.lessonArrayList.get(GlobalData.lessonsPlayingPosition).title
        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}
            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                GlobalData.lessonsPlayingPosition = simpleExoPlayer.currentWindowIndex
                playerTitle.text = GlobalData.lessonArrayList.get(GlobalData.lessonsPlayingPosition).title
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                    }
                    Player.STATE_ENDED -> {
                    }
                    Player.STATE_IDLE -> {
                    }
                    Player.STATE_READY -> {
                    }
                    else -> {
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {}
            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlayerError(error: ExoPlaybackException) {
                error.printStackTrace()
                helperMethods.showToastMessage(getString(R.string.this_mobile_not_capable_for_playing_this_video))
            }

            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {
            }
        })
    }

    fun lessonCompletedApiCall(course_id: String, resource_id: String, lesson_id: String) {
//        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.LESSON_COMPLETED_API_CALL("Bearer ${dataUser.access_token}", course_id, resource_id, lesson_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
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
//                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.stop()
        simpleExoPlayer.release()
        handler.removeCallbacks(runnable)
    }
}