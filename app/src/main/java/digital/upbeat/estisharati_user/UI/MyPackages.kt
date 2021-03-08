package digital.upbeat.estisharati_user.UI

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.PackageAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.CarouselHelper.CenterScrollListener
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.Packages.PackagesResponse
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_my_courses.*
import kotlinx.android.synthetic.main.activity_my_packages.*
import kotlinx.android.synthetic.main.activity_my_packages.emptyLayout
import kotlinx.android.synthetic.main.activity_my_packages.errorText
import kotlinx.android.synthetic.main.activity_my_packages.nav_back
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyPackages : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var packagesResponse: PackagesResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_packages)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            MYPACKAGES_API_CALL()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(this@MyPackages)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        sharedPreferencesHelper = SharedPreferencesHelper(this@MyPackages)
        dataUser = sharedPreferencesHelper.logInUser
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
    }

    fun InitializeRecyclerview() {
        myPackageRecycler.addOnScrollListener(CenterScrollListener())
        myPackageRecycler.setHasFixedSize(true)
        myPackageRecycler.removeAllViews()
        myPackageRecycler.layoutManager = LinearLayoutManager(this@MyPackages)
        myPackageRecycler.adapter = PackageAdapter(this@MyPackages, null, this@MyPackages,null, packagesResponse.data, arrayListOf())
        if (packagesResponse.data.size > 0) {
            emptyLayout.visibility = View.GONE
            myPackageRecycler.visibility = View.VISIBLE
        } else {
            errorText.text = getString(R.string.you_did_not_purchase_any_packages_till_now)
            emptyLayout.visibility = View.VISIBLE
            myPackageRecycler.visibility = View.GONE
        }
    }

    fun MYPACKAGES_API_CALL() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.MYPACKAGES_API_CALL("Bearer ${dataUser.access_token}")
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