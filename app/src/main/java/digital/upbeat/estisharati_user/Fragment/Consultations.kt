package digital.upbeat.estisharati_user.Fragment

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import digital.upbeat.estisharati_user.Adapter.ConsultationsAdapter
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.Category.CategoriesResponse
import digital.upbeat.estisharati_user.DataClassHelper.Category.Data
import digital.upbeat.estisharati_user.DataClassHelper.Category.Subcategory
import digital.upbeat.estisharati_user.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.UI.UserDrawer
import kotlinx.android.synthetic.main.app_bar_user_drawer.*
import kotlinx.android.synthetic.main.fragment_consultations.*
import okhttp3.ResponseBody
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class Consultations(val userDrawer: UserDrawer) : Fragment() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUser: DataUser
    lateinit var categoriesResponse: CategoriesResponse
    var categoryArrayList: ArrayList<Data> = arrayListOf()
    var subCategoryArrayList: ArrayList<Subcategory> = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consultations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        clickEvents()
        if (helperMethods.isConnectingToInternet) {
            categoriesApiCall()
        } else {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
        }
    }

    fun initViews() {
        helperMethods = HelperMethods(requireContext())
        preferencesHelper = SharedPreferencesHelper(requireContext())
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUser = preferencesHelper.logInUser
    }

    fun clickEvents() {
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action === KeyEvent.ACTION_UP) {
                if (subCategoryArrayList.size > 0) {
                    InitializeRecyclerview(categoriesResponse.data, arrayListOf())
                    return@OnKeyListener true
                } else if (categoryArrayList.size > 0) {
                    userDrawer.navHome()
                    return@OnKeyListener true
                }
            }
            true
        })
    }

    fun InitializeRecyclerview(categoryArrayList: ArrayList<Data>, subCategoryArrayList: ArrayList<Subcategory>) {
        this.categoryArrayList = categoryArrayList
        this.subCategoryArrayList = subCategoryArrayList
        consultations_recycler.setHasFixedSize(true)
        consultations_recycler.removeAllViews()
        consultations_recycler.layoutManager = LinearLayoutManager(requireContext())
        consultations_recycler.adapter = ConsultationsAdapter(requireContext(), this@Consultations, categoryArrayList, subCategoryArrayList)
        if (categoryArrayList.size > 0 || subCategoryArrayList.size > 0) {
            consultations_recycler.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
        } else {
            consultations_recycler.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        }
    }

    fun categoriesApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.CATEGORIES_API_CALL("Bearer ${dataUser.access_token}")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            categoriesResponse = Gson().fromJson(response.body()!!.string(), CategoriesResponse::class.java)
                            if (categoriesResponse.status.equals("200")) {
                                InitializeRecyclerview(categoriesResponse.data, arrayListOf())
                            } else {
                                if (helperMethods.checkTokenValidation(categoriesResponse.status, categoriesResponse.message)) {
                                    requireActivity().finish()
                                    return
                                }
                                helperMethods.AlertPopup(getString(R.string.alert), categoriesResponse.message)
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
