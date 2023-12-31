package estisharatibussiness.users.com.UserInterfaces

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import estisharatibussiness.users.com.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.gson.Gson
import estisharatibussiness.users.com.AdapterClasses.TapViewPagerAdapter
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.StartCourse.StartCourseResponse
import estisharatibussiness.users.com.FragmentClasses.*
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_course_resource.*
import kotlinx.android.synthetic.main.activity_course_resource.courseTablayout
import kotlinx.android.synthetic.main.activity_course_resource.courseViewpager
import kotlinx.android.synthetic.main.activity_course_resource.nav_back
import kotlinx.android.synthetic.main.tap_item.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivityCourseResource : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var startCourseResponse: StartCourseResponse
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var courseVideo: CourseVideos
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
        setContentView(R.layout.activity_course_resource)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            intent.getStringExtra("courseId")?.let { startCourseApiCall(it) }
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityCourseResource)
        preferencesHelper = SharedPreferencesHelper(this@ActivityCourseResource)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        simpleExoPlayer = SimpleExoPlayer.Builder(this@ActivityCourseResource).build()
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        fullScreen.setOnClickListener {
            if (startCourseResponse.data.videos.size > 0) {
                GlobalData.FullScreen = true
                startActivity(Intent(this@ActivityCourseResource, ActivityVideoFullScreen::class.java))
            }
        }
    }

    fun setUpPlayer() {
        for (videoIndex in startCourseResponse.data.videos.indices) {
            for (index in startCourseResponse.data.videos.get(videoIndex).lessons.indices) {
                val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(startCourseResponse.data.videos.get(videoIndex).lessons.get(index).lesson_file))
                GlobalData.mediaItemArrayList.add(mediaItem)
                GlobalData.lessonArrayList.add(startCourseResponse.data.videos.get(videoIndex).lessons.get(index))
                startCourseResponse.data.videos.get(videoIndex).lessons.get(index).position = GlobalData.mediaItemArrayList.lastIndex
            }
        }

        exoPlayer.player=simpleExoPlayer
        simpleExoPlayer.setMediaItems(GlobalData.mediaItemArrayList)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()

        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {}
            override fun onTracksChanged(tracks: Tracks) {
                GlobalData.lessonsPlayingPosition = simpleExoPlayer.currentWindowIndex
                courseVideo.InitializeRecyclerview()
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
            override fun onPlayerError(error: PlaybackException) {
                error.printStackTrace()
                helperMethods.showToastMessage(getString(R.string.this_mobile_not_capable_for_playing_this_video))
            }

            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {
            }
        })
    }

    fun changePlayerPosition(position: Int) {
        simpleExoPlayer.seekTo(position, C.TIME_UNSET)
    }

    fun tapInitialize() {
        if (startCourseResponse.data.videos.size > 0) {
            GlobalData.lessonsPlayingPosition = 0
            GlobalData.mediaItemArrayList.clear()
            GlobalData.lessonArrayList.clear()
            setUpPlayer()
        }
        val tabOne = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabTwo = layoutInflater.inflate(R.layout.tap_item, null) as View
        tabOne.tap_text.text = getString(R.string.course_videos)
        tabTwo.tap_text.text = getString(R.string.documents)
        setUpViewPager(courseViewpager)
        courseTablayout.setupWithViewPager(courseViewpager)
        courseTablayout.getTabAt(0)?.customView = tabOne
        courseTablayout.getTabAt(1)?.customView = tabTwo
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        courseVideo = CourseVideos(this@ActivityCourseResource)
        val adapter = TapViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(courseVideo, "ONE")
        adapter.addFrag(CourseDocuments(this@ActivityCourseResource), "TWO")
        viewPager.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        if (GlobalData.FullScreen) {
            GlobalData.FullScreen = false
            simpleExoPlayer.seekTo(GlobalData.lessonsPlayingPosition, GlobalData.lessonsPlayingDuration)
            simpleExoPlayer.prepare()
            simpleExoPlayer.play()
        }
        handler.postDelayed(runnable, 1000)
    }

    override fun onStop() {
        simpleExoPlayer.stop()
        handler.removeCallbacks(runnable)

        super.onStop()
    }

    override fun onDestroy() {
        simpleExoPlayer.stop()
        simpleExoPlayer.release()
        super.onDestroy()
    }

    fun lessonCompletedApiCall(course_id: String, resource_id: String, lesson_id: String) {
        //        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.LESSON_COMPLETED_API_CALL("Bearer ${dataUser.access_token}", course_id, resource_id, lesson_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val message = jsonObject.getString("message")
                                //                                helperMethods.showToastMessage(message)
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
                //                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun startCourseApiCall(course_id: String) {
        Log.d("course_ID",course_id)
        Log.d("firebase_barrier_token","${dataUser.access_token}")
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.START_COURSE_API_CALL("Bearer ${dataUser.access_token}", course_id)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            startCourseResponse = Gson().fromJson(response.body()!!.string(), StartCourseResponse::class.java)
                            if (startCourseResponse.status.equals("200")) {
                                tapInitialize()
                            } else {
                                if (helperMethods.checkTokenValidation(startCourseResponse.status, startCourseResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), startCourseResponse.message)
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