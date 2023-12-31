package estisharatibussiness.users.com.UserInterfaces

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import estisharatibussiness.users.com.AdapterClasses.PackageAdapter
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.CarouselSlidders.CarouselLayoutManager
import estisharatibussiness.users.com.CarouselSlidders.CarouselZoomPostLayoutListener
import estisharatibussiness.users.com.CarouselSlidders.CenterScrollListener
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.DataClassHelperMehtods.Packages.PackagesResponse
import estisharatibussiness.users.com.DataClassHelperMehtods.PackagesOptions.PackagesOptions
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_packages.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ActivityPackages : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var packagesResponse: PackagesResponse
    var viaFrom = ""
    var packageId = ""
    val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packages)
        initViews()
        clickEvents1()
        if (helperMethods.isConnectingToInternet) {
            SUBSCRIPTIONS_API_CALL()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
        viaFrom = intent.getStringExtra("viaFrom").toString()
        intent.getStringExtra("package_id")?.let {
            packageId = it
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityPackages)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivityPackages)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        choose_the_package.setOnClickListener {
            val packages = packagesResponse.data.get(layoutManager.centerItemPosition) //            val vatAmount = packages.price.toDouble() * 0.05
            //            val priceIncludedVat = vatAmount + packages.price.toDouble()
            val price = if (packages.offerprice.equals("0")) packages.price else packages.offerprice



            GlobalData.packagesOptions = PackagesOptions(packages.id, packages.name, "subscription", "", "0", "0", "0", price, "0", "0", "", "", "0", "0", "", "0", "0")
            if (viaFrom.equals("Home")) {
                startActivity(Intent(this@ActivityPackages, ActivityPackagesSelection::class.java))
            } else {
                finish()
            }
        }
    }

    fun clickEvents1() {

        nav_back.setOnClickListener { finish() }

        choose_the_package.setOnClickListener {

            val packages = packagesResponse.data.get(layoutManager.centerItemPosition)
            val price = if (packages.offerprice.equals("0")) packages.price else packages.offerprice
            GlobalData.packagesOptions = PackagesOptions(packages.id, packages.name, "subscription", "", "0", "0", "0", price, "0", "0", "", "", "0", "0", "", "0", "0")

            val intent = Intent(this, ConsultantsInSideThePackageActivity::class.java)
            intent.putExtra("consultants",packagesResponse.data[layoutManager.centerItemPosition].consultants)
            intent.putExtra("price",price)
            intent.putExtra("audioF",packagesResponse.data[layoutManager.centerItemPosition].features.audio.time)
            intent.putExtra("videoF",packagesResponse.data[layoutManager.centerItemPosition].features.video.time)
            intent.putExtra("chatF",packagesResponse.data[layoutManager.centerItemPosition].features.written.time)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            this.finish()
        }
    }

    fun InitializeRecyclerview() {
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        package_recycler.addOnScrollListener(CenterScrollListener())
        package_recycler.setHasFixedSize(true)
        package_recycler.removeAllViews()
        package_recycler.layoutManager = layoutManager
        package_recycler.adapter = PackageAdapter(this@ActivityPackages, this@ActivityPackages, null, null, packagesResponse.data, arrayListOf())
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
        if (packageId.isNotEmpty()) {
            for (packageIndex in packagesResponse.data.indices) {
                if (packagesResponse.data[packageIndex].id == packageId) {
                    package_recycler.scrollToPosition(packageIndex)
                }
            }
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