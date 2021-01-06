package digital.upbeat.estisharati_user.UI

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
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.TapViewPagerAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.CourseDetails.ResponseCourseDetails
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.PackagesOptions.PackagesOptions
import digital.upbeat.estisharati_user.Fragment.Comments
import digital.upbeat.estisharati_user.Fragment.CourseContent
import digital.upbeat.estisharati_user.Fragment.Instructor
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_course_details.*
import kotlinx.android.synthetic.main.preview_courses_popup.view.*
import kotlinx.android.synthetic.main.tap_item.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CourseDetails : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var dataUser: DataUser
    lateinit var responseCoursesDetails: ResponseCourseDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_details)
        initViews()
        clickEvents()

        if (helperMethods.isConnectingToInternet) {
            onlineCoursesApiCall(intent.getStringExtra("courseId"))
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@CourseDetails)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@CourseDetails)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        showMore.setOnClickListener {
            helperMethods.AlertPopup(getString(R.string.course_description), responseCoursesDetails.description)
        }
        previewCourse.setOnClickListener {
            showCoursePreviewPopup()
        }


        buyTheCourse.setOnClickListener {
            if (responseCoursesDetails.is_subscribed) {
                val intent = Intent(this@CourseDetails, CourseResource::class.java)
                intent.putExtra("courseId", responseCoursesDetails.id)
                startActivity(intent)
            } else {
                val price = if (responseCoursesDetails.offerprice.equals("0")) {
                    responseCoursesDetails.price
                } else {
                    responseCoursesDetails.offerprice
                }
                val vatAmount: Float = price.toFloat() / 100.0f * 5
                val priceIncludedVat = vatAmount + price.toFloat()
                GlobalData.packagesOptions = PackagesOptions(responseCoursesDetails.id, responseCoursesDetails.name, "course", "", price, vatAmount.toString(), priceIncludedVat.toString(), "", "", "")

                startActivity(Intent(this@CourseDetails, PackagesSelection::class.java))
            }
        }
        favoriteLayout.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                addRemoveFavouriteCourseApiCall(responseCoursesDetails.id)
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
    }

    fun setCourseDetails() {
        if (responseCoursesDetails.favourite) {
            favoriteIcon.setImageResource(R.drawable.ic_favorite)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_un_favorite)
        }
        courseName.text = responseCoursesDetails.name
        courseDescription.text = helperMethods.getHtmlText(responseCoursesDetails.description)
        courseRating.text = responseCoursesDetails.rate
        courseHours.text = getString(R.string.hours) + " " + responseCoursesDetails.course_duration
        courseVideos.text = getString(R.string.videos) + " " + responseCoursesDetails.videos_count
        courseChapters.text = getString(R.string.chapters) + " " + responseCoursesDetails.chapters_count
        coursePeriod.text = responseCoursesDetails.period
        Glide.with(this@CourseDetails).load(responseCoursesDetails.image_path).apply(helperMethods.requestOption).into(courseBackgroundImage)
        Glide.with(this@CourseDetails).load(responseCoursesDetails.image_path).apply(helperMethods.requestOption).into(previewCourse)
        if (responseCoursesDetails.offerprice.equals("0")) {
            coursePrice.text = "${getString(R.string.aed)} ${responseCoursesDetails.price}"
            courseOldPrice.visibility = View.GONE
            offersEndDateLayout.visibility = View.GONE
        } else {
            coursePrice.text = "${getString(R.string.aed)} ${responseCoursesDetails.offerprice}"
            courseOldPrice.text = "${getString(R.string.aed)} ${responseCoursesDetails.price}"
            offersEndDate.text = responseCoursesDetails.offer_end
            courseOldPrice.setPaintFlags(courseOldPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            courseOldPrice.visibility = View.VISIBLE
            offersEndDateLayout.visibility = View.VISIBLE
        }
        courseDescriptionFull.text = helperMethods.getHtmlText(responseCoursesDetails.description)
        if (responseCoursesDetails.downloadable) {
            download_video.setText(R.string.ability_to_download_course_videos)
        } else {
            download_video.setText(R.string.you_can_not_download_course_videos)
        }
        buyTheCourse.text = if (responseCoursesDetails.is_subscribed) resources.getString(R.string.start_course) else {
            resources.getString(R.string.buy_now)
        }
    }

    fun showCoursePreviewPopup() {
        val layoutView = LayoutInflater.from(this@CourseDetails).inflate(R.layout.preview_courses_popup, null)
        val aleatdialog = AlertDialog.Builder(this@CourseDetails)
        aleatdialog.setView(layoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        layoutView.courseName.text = responseCoursesDetails.name
        val simpleExoPlayer = SimpleExoPlayer.Builder(this@CourseDetails).build()
        val uri = Uri.parse(responseCoursesDetails.video_path)
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        layoutView.exoPlayer.setPlayer(simpleExoPlayer)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()
        layoutView.closePopup.setOnClickListener {
            simpleExoPlayer.stop()
            simpleExoPlayer.release()
            dialog.dismiss()
        }
    }

    fun tapInitialize() {
        val tabOne = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabTwo = layoutInflater.inflate(R.layout.tap_item, null) as View
        val tabThree = layoutInflater.inflate(R.layout.tap_item, null) as View
        tabOne.tap_text.text = getString(R.string.course_content)
        tabTwo.tap_text.text = getString(R.string.instructor)
        tabThree.tap_text.text = getString(R.string.comments)
        setUpViewPager(courseViewpager)
        courseTablayout.setupWithViewPager(courseViewpager)
        courseTablayout.getTabAt(0)?.setCustomView(tabOne)
        courseTablayout.getTabAt(1)?.setCustomView(tabTwo)
        courseTablayout.getTabAt(2)?.setCustomView(tabThree)
    }

    private fun setUpViewPager(viewPager: ViewPager) {
        val adapter = TapViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(CourseContent(this@CourseDetails), "ONE")
        adapter.addFrag(Instructor(this@CourseDetails), "TWO")
        adapter.addFrag(Comments(this@CourseDetails), "THREE")
        viewPager.setAdapter(adapter)
    }

    fun onlineCoursesApiCall(courseId: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.COURSES_DETAILS_API_CALL("Bearer ${dataUser.access_token}", courseId)
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
                                responseCoursesDetails = Gson().fromJson(dataString, ResponseCourseDetails::class.java)
                                tapInitialize()
                                setCourseDetails()
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

    fun addRemoveFavouriteCourseApiCall(courseId: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.FAVOURITE_COURSE_API_CALL("Bearer ${dataUser.access_token}", courseId)
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
                                responseCoursesDetails.favourite = favorite
                                if (responseCoursesDetails.favourite) {
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