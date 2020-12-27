package digital.upbeat.estisharati_user.UI

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.TapViewPagerAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.StartCourse.Lesson
import digital.upbeat.estisharati_user.DataClassHelper.StartCourse.StartCourseResponse
import digital.upbeat.estisharati_user.Fragment.*
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_course_details.*
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

class CourseResource : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var simpleExoPlayer: SimpleExoPlayer
    lateinit var startCourseResponse: StartCourseResponse
    var lessonsPlayingPosition = 0
    lateinit var lessonsPlaying: ImageView
    val mediaItemArrayList: ArrayList<MediaItem> = arrayListOf();
    val lessonArrayList: ArrayList<Lesson> = arrayListOf();
    lateinit var courseVideo: CourseVideos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_resource)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            startCourseApiCall(intent.getStringExtra("courseId"))
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@CourseResource)
        preferencesHelper = SharedPreferencesHelper(this@CourseResource)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        simpleExoPlayer = SimpleExoPlayer.Builder(this@CourseResource).build()
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun setUpPlayer() {
        for (videoIndex in startCourseResponse.data.videos.indices) {
            for (index in startCourseResponse.data.videos.get(videoIndex).lessons.indices) {
                val mediaItem: MediaItem = MediaItem.fromUri(Uri.parse(startCourseResponse.data.videos.get(videoIndex).lessons.get(index).lesson_file))
                mediaItemArrayList.add(mediaItem)
                lessonArrayList.add(startCourseResponse.data.videos.get(videoIndex).lessons.get(index))
                startCourseResponse.data.videos.get(videoIndex).lessons.get(index).position = mediaItemArrayList.lastIndex
            }
        }
        if (!lessonArrayList.get(0).watched) {
            lessonArrayList.get(0).watched = true
            lessonCompletedApiCall(lessonArrayList.get(0).course_id, lessonArrayList.get(0).chapter_id, lessonArrayList.get(0).id)
        }
        exoPlayer.setPlayer(simpleExoPlayer)
        simpleExoPlayer.setMediaItems(mediaItemArrayList)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
        simpleExoPlayer.currentWindowIndex
        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}
            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                lessonsPlayingPosition = simpleExoPlayer.currentWindowIndex
                courseVideo.InitializeRecyclerview()
                if (!lessonArrayList.get(lessonsPlayingPosition).watched) {
                    lessonArrayList.get(lessonsPlayingPosition).watched = true
                    lessonCompletedApiCall(lessonArrayList.get(lessonsPlayingPosition).course_id, lessonArrayList.get(lessonsPlayingPosition).chapter_id, lessonArrayList.get(lessonsPlayingPosition).id)
                }
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
            override fun onPlayerError(error: ExoPlaybackException) {}
            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
            override fun onSeekProcessed() {
            }
        })
    }

    fun changePlayerPosition(position: Int) {
        simpleExoPlayer.seekTo(position, C.TIME_UNSET)
        if (!lessonArrayList.get(position).watched) {
            lessonArrayList.get(position).watched = true
            lessonCompletedApiCall(lessonArrayList.get(position).course_id, lessonArrayList.get(position).chapter_id, lessonArrayList.get(position).id)
        }
    }

    fun tapInitialize() {
        if (startCourseResponse.data.videos.size > 0) {
            setUpPlayer()
            lessonsPlayingPosition = 0
        }
        val tabOne = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabTwo = layoutInflater.inflate(R.layout.tap_item, null) as View
        tabOne.tap_text.text = "Course Videos"
        tabTwo.tap_text.text = "Documents"
        setUpViewPager(courseViewpager)
        courseTablayout.setupWithViewPager(courseViewpager)
        courseTablayout.getTabAt(0)?.setCustomView(tabOne)
        courseTablayout.getTabAt(1)?.setCustomView(tabTwo)
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        courseVideo = CourseVideos(this@CourseResource)
        val adapter = TapViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(courseVideo, "ONE")
        adapter.addFrag(CourseDocuments(this@CourseResource), "TWO")
        viewPager.setAdapter(adapter)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        simpleExoPlayer.stop()
        super.onStop()
    }

    override fun onDestroy() {
        simpleExoPlayer.stop()
        simpleExoPlayer.release()
        super.onDestroy()
    }

    fun lessonCompletedApiCall(course_id: String, resource_id: String, lesson_id: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
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
                                helperMethods.AlertPopup("Alert", message)
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
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun startCourseApiCall(course_id: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
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
                                val message = JSONObject(response.body()!!.string()).getString("message")
                                helperMethods.AlertPopup("Alert", message)
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
                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
}