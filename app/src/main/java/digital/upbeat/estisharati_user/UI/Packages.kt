package digital.upbeat.estisharati_user.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.PackageAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.CarouselHelper.CarouselLayoutManager
import digital.upbeat.estisharati_user.CarouselHelper.CarouselZoomPostLayoutListener
import digital.upbeat.estisharati_user.CarouselHelper.CenterScrollListener
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Packages.PackagesResponse
import digital.upbeat.estisharati_user.DataClassHelper.PackagesOptions.PackagesOptions
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_packages.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class Packages : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var packagesResponse: PackagesResponse
    var viaFrom = ""
    val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packages)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            SUBSCRIPTIONS_API_CALL()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
        viaFrom = intent.getStringExtra("viaFrom")
    }

    fun initViews() {
        helperMethods = HelperMethods(this@Packages)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@Packages)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        choose_the_package.setOnClickListener {
            val packages = packagesResponse.data.get(layoutManager.centerItemPosition)
            //            val vatAmount = packages.price.toDouble() * 0.05
            //            val priceIncludedVat = vatAmount + packages.price.toDouble()
            GlobalData.packagesOptions = PackagesOptions(packages.id, packages.name, "subscription", "", "0","0","0",packages.price, "0", "0", "", "", "0", "0", "", "0", "0")
            if (viaFrom.equals("Home")) {
                startActivity(Intent(this@Packages, PackagesSelection::class.java))
            } else {
                finish()
            }
        }
    }

    fun InitializeRecyclerview() {
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        package_recycler.addOnScrollListener(CenterScrollListener())
        package_recycler.setHasFixedSize(true)
        package_recycler.removeAllViews()
        package_recycler.layoutManager = layoutManager
        package_recycler.adapter = PackageAdapter(this@Packages, this@Packages, null, packagesResponse.data)
        if (packagesResponse.data.size > 0) {
            emptyLayout.visibility = View.GONE
            package_recycler.visibility = View.VISIBLE
            choose_the_package.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.there_is_no_packages_available)
            emptyLayout.visibility = View.VISIBLE
            package_recycler.visibility = View.GONE
            choose_the_package.visibility = View.GONE
        }
    }

    fun SUBSCRIPTIONS_API_CALL() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.SUBSCRIPTIONS_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            packagesResponse = Gson().fromJson(response.body()!!.string(), PackagesResponse::class.java)
                            if (packagesResponse.status.equals("200")) {
                                InitializeRecyclerview()
                            } else {
                                if (helperMethods.checkTokenValidation(packagesResponse.status, packagesResponse.message)) {
                                    finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), packagesResponse.message)
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