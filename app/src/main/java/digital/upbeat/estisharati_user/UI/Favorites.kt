package digital.upbeat.estisharati_user.UI

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.FavoriteConsultantAdapter
import digital.upbeat.estisharati_user.Adapter.FavoriteCoursesAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Favourites.FavouritesResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_favorites.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Favorites : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    var favoritesResponse: FavouritesResponse = FavouritesResponse(arrayListOf(), arrayListOf())
    var currentTab = "consultant"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        initViews()
        clickEvents()
    }

    override fun onStart() {
        super.onStart()
        if (helperMethods.isConnectingToInternet) {
            FavouriteListApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Favorites)
        helperMethods = HelperMethods(this@Favorites)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@Favorites)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        favoriteConsultantRecycler.visibility = View.VISIBLE
        favoriteCoursesRecycler.visibility = View.GONE
        consultantsTab.setTextColor(ContextCompat.getColor(this@Favorites, R.color.white))
        coursesTab.setTextColor(ContextCompat.getColor(this@Favorites, R.color.transparent_white))
        consultantsTab.setOnClickListener {
            currentTab = "consultant"
            setTabConsultant()
        }
        coursesTab.setOnClickListener {
            currentTab = "courses"
            setTabCourse()
        }
    }

    fun setTabConsultant() {
        if (currentTab.equals("consultant")) {
            if (favoritesResponse.consultants.size > 0) {
                favoriteConsultantRecycler.visibility = View.VISIBLE
                favoriteCoursesRecycler.visibility = View.GONE
                emptyLayout.visibility = View.GONE
            } else {
                favoriteConsultantRecycler.visibility = View.GONE
                favoriteCoursesRecycler.visibility = View.GONE
                emptyLayout.visibility = View.VISIBLE
                errorText.text = "There is no favorite consultant available !"
            }
            consultantsTab.setTextColor(ContextCompat.getColor(this@Favorites, R.color.white))
            coursesTab.setTextColor(ContextCompat.getColor(this@Favorites, R.color.transparent_white))
        }
    }

    fun setTabCourse() {
        if (currentTab.equals("courses")) {
            if (favoritesResponse.courses.size > 0) {
                favoriteCoursesRecycler.visibility = View.VISIBLE
                favoriteConsultantRecycler.visibility = View.GONE
                emptyLayout.visibility = View.GONE
            } else {
                favoriteConsultantRecycler.visibility = View.GONE
                favoriteCoursesRecycler.visibility = View.GONE
                emptyLayout.visibility = View.VISIBLE
                errorText.text = "There is no favorite courses available !"
            }
            coursesTab.setTextColor(ContextCompat.getColor(this@Favorites, R.color.white))
            consultantsTab.setTextColor(ContextCompat.getColor(this@Favorites, R.color.transparent_white))
        }
    }

    fun initializeFavoriteConsultantRecyclerview() {
        favoriteConsultantRecycler.setHasFixedSize(true)
        favoriteConsultantRecycler.removeAllViews()
        favoriteConsultantRecycler.layoutManager = LinearLayoutManager(this@Favorites)
        favoriteConsultantRecycler.adapter = FavoriteConsultantAdapter(this@Favorites, this@Favorites, favoritesResponse.consultants)
        setTabConsultant()
    }

    fun initializeFavoriteCoursesRecyclerview() {
        favoriteCoursesRecycler.setHasFixedSize(true)
        favoriteCoursesRecycler.removeAllViews()
        favoriteCoursesRecycler.layoutManager = LinearLayoutManager(this@Favorites)
        favoriteCoursesRecycler.adapter = FavoriteCoursesAdapter(this@Favorites, this@Favorites, favoritesResponse.courses)
        setTabCourse()
    }

    fun FavouriteListApiCall() {
        helperMethods.showProgressDialog("Please wait while loading...")
        val responseBodyCall = retrofitInterface.FAVOURITES_LIST_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val data = jsonObject.getString("data")
                                favoritesResponse = Gson().fromJson(data, FavouritesResponse::class.java)
                                initializeFavoriteConsultantRecyclerview()
                                initializeFavoriteCoursesRecyclerview()
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
}