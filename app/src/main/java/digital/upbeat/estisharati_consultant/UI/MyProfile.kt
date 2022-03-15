package digital.upbeat.estisharati_consultant.UI

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_consultant.DataClassHelper.CountryCity.DataCity
import digital.upbeat.estisharati_consultant.DataClassHelper.CountryCity.DataCountry
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.*
import digital.upbeat.estisharati_consultant.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_my_profile.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MyProfile : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUserObject: DataUser
    lateinit var LayoutView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@MyProfile)
        preferencesHelper = SharedPreferencesHelper(this@MyProfile)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    override fun onStart() {
        super.onStart()
        setUserDetils()
    }
    fun setUserDetils() {
        dataUserObject = preferencesHelper.logInConsultant
        user_name.text = dataUserObject.fname + " " + dataUserObject.lname
        jobTitle.text = dataUserObject.user_metas.job_title
        phone.text = dataUserObject.phone
        courses_count.text = GlobalData.mySubscriberResponse.course_count
        consultations_count.text = GlobalData.mySubscriberResponse.consultation_count
        email_address.text = dataUserObject.email
        Glide.with(this@MyProfile).load(dataUserObject.image).apply(helperMethods.profileRequestOption).into(profile_picture)
        qualification_name.text = helperMethods.getFileNameFromURL(dataUserObject.user_metas.qualification)
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        change_password_layot.visibility = View.GONE
        change_password_arrow.setImageResource(R.drawable.ic_up_arrow_white)

        password_btn.setOnClickListener {
            if (change_password_layot.visibility == View.VISIBLE) {
                change_password_layot.visibility = View.GONE
                change_password_arrow.setImageResource(R.drawable.ic_up_arrow_white)
            } else {
                change_password_layot.visibility = View.VISIBLE
                change_password_arrow.setImageResource(R.drawable.ic_down_arrow_white)
                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
        profile_picture_layout.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                if (preferencesHelper.countryCity.size > 0) {
                    startActivity(Intent(this@MyProfile, MyProfileUpdate::class.java))
                } else {
                    countryCityApiCall()
                }
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        edit_profile.setOnClickListener {}
        savePassword.setOnClickListener {
            if (validateUpdatePassword()) {
                ChangePasswordApiCall(currentPassword.toText(), newPassword.toText(), confirmPassword.toText())
            }
        }
    }

    fun ChangePasswordApiCall(currentPassword: String, newPassword: String, confirmPassword: String) {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.CHANGE_PASSWORD_API_CALL("Bearer ${dataUserObject.access_token}", currentPassword, newPassword, confirmPassword)
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
                                change_password_layot.visibility = View.GONE
                                change_password_arrow.setImageResource(R.drawable.ic_up_arrow_white)
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
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }

    fun validateUpdatePassword(): Boolean {
        if (currentPassword.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_current_password))
            return false
        }
        if (newPassword.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_new_password))
            return false
        }
        if (!helperMethods.isValidPassword(newPassword.toText())) {
            helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.password_at_least_8_characters_including_a_lower_case_letteran_uppercase_lettera_number_and_one_special_character))
            return false
        }
        if (confirmPassword.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_confirm_password))
            return false
        }
        if (!newPassword.toText().equals(confirmPassword.toText())) {
            helperMethods.showToastMessage(getString(R.string.new_password_and_confirm_password_not_same))
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun countryCityApiCall() {
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.GEOGRAPHIES_API_CALL("Bearer ${dataUserObject.access_token}")
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
                                val countryArray = JSONArray(dataString)
                                val countryArrayList = arrayListOf<DataCountry>()
                                for (country_index in 0 until countryArray.length()) {
                                    val countryObject = countryArray.getJSONObject(country_index)
                                    val country_id = countryObject.getString("country_id")
                                    val country_name = countryObject.getString("country_name")
                                    val citiesStr = countryObject.getString("cities")
                                    val cityArray = JSONArray(citiesStr)
                                    val cities = arrayListOf<DataCity>()
                                    for (city_index in 0 until cityArray.length()) {
                                        val cityObject = cityArray.getJSONObject(city_index)
                                        val city_id = cityObject.getString("city_id")
                                        val city_name = cityObject.getString("city_name")
                                        cities.add(DataCity(city_id, city_name))
                                    }
                                    countryArrayList.add(DataCountry(country_id, country_name, cities))
                                }
                                preferencesHelper.countryCity = countryArrayList
                                startActivity(Intent(this@MyProfile, MyProfileUpdate::class.java))
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
                helperMethods.dismissProgressDialog()
                t.printStackTrace()
                helperMethods.AlertPopup(getString(R.string.alert), getString(R.string.your_network_connection_is_slow_please_try_again))
            }
        })
    }
    //    fun profileEditPopup() {
    //
    //        var countryIndex = 0
    //        var cityIndex = 0
    //        val countryArrString = arrayListOf<String>()
    //        for (index in countryArrayList.indices) {
    //            countryArrString.add(countryArrayList.get(index).country_name)
    //            if (countryArrayList.get(index).country_id.equals(dataUserObject.user_metas.country)) {
    //                countryIndex = index
    //            }
    //        }
    //        val typeface = ResourcesCompat.getFont(this@MyProfile, R.font.almarai_regular)
    //        val countryAdapter = ArrayAdapter(this@MyProfile, R.layout.support_simple_spinner_dropdown_item, countryArrString)
    //        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
    //        LayoutView.ud_country_spinner.adapter = countryAdapter
    //        LayoutView.ud_country_spinner.setSelection(countryIndex, true)
    //        val v2 = LayoutView.ud_country_spinner.selectedView
    //        (v2 as TextView).textSize = 15f
    //        v2.typeface = typeface
    //        v2.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
    //        LayoutView.ud_country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    //            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
    //                (view as TextView).textSize = 15f
    //                view.typeface = typeface
    //                view.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
    //                val cityArrString = arrayListOf<String>()
    //                for (city in countryArrayList.get(position).cities) {
    //                    cityArrString.add(city.city_name)
    //                }
    //                val cityAdapter = ArrayAdapter(this@MyProfile, R.layout.support_simple_spinner_dropdown_item, cityArrString)
    //                cityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
    //                LayoutView.ud_city_spinner.adapter = cityAdapter
    //            }
    //
    //            override fun onNothingSelected(parent: AdapterView<*>) {
    //            }
    //        }
    //        val cityArrString = arrayListOf<String>()
    //        for (index in countryArrayList.get(countryIndex).cities.indices) {
    //            cityArrString.add(countryArrayList.get(countryIndex).cities.get(index).city_name)
    //            if (countryArrayList.get(countryIndex).cities.get(index).city_id.equals(dataUserObject.user_metas.city)) {
    //                cityIndex = index
    //            }
    //        }
    //        val cityAdapter = ArrayAdapter(this@MyProfile, R.layout.support_simple_spinner_dropdown_item, cityArrString)
    //        cityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
    //        LayoutView.ud_city_spinner.adapter = cityAdapter
    //        LayoutView.ud_city_spinner.setSelection(cityIndex, true)
    //        val v3 = LayoutView.ud_city_spinner.selectedView
    //        (v3 as TextView).textSize = 15f
    //        v3.typeface = typeface
    //        v3.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
    //        LayoutView.ud_city_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    //            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
    //                (view as TextView).textSize = 15f
    //                view.typeface = typeface
    //                view.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
    //            }
    //
    //            override fun onNothingSelected(parent: AdapterView<*>) {
    //            }
    //        }
    //        /*******Spinner end***********/
    //
    //
    //
    //
    //    }
    //    fun profileUpdateApiCall(fNmae: String, lName: String, emailAddress: String, countryID: String, cityID: String) {
    //        helperMethods.showProgressDialog("Profile is updating...")
    //        val responseBodyCall = retrofitInterface.PROFILE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", fNmae, lName, emailAddress, countryID, cityID)
    //        responseBodyCall.enqueue(object : Callback<ResponseBody> {
    //            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
    //                helperMethods.dismissProgressDialog()
    //
    //                if (response.isSuccessful) {
    //                    if (response.body() != null) {
    //                        try {
    //                            val jsonObject = JSONObject(response.body()!!.string())
    //                            val status = jsonObject.getString("status")
    //                            if (status.equals("200")) {
    //                                val userString = jsonObject.getString("user")
    //                                val userObject = JSONObject(userString)
    //                                val id = userObject.getString("id")
    //                                val fname = userObject.getString("fname")
    //                                val lname = userObject.getString("lname")
    //                                val email = userObject.getString("email")
    //                                val phone = userObject.getString("phone")
    //                                val image = userObject.getString("image")
    //                                val member_since = userObject.getString("member_since")
    //                                val user_metasStr = userObject.getString("user_metas")
    //                                val userMetasObject = JSONObject(user_metasStr)
    //                                val city = userMetasObject.getString("city")
    //                                val phone_code = userMetasObject.getString("phone_code")
    //                                val country = userMetasObject.getString("country")
    //                                val fire_base_token = userMetasObject.getString("fire_base_token")
    //                                val user_metas = DataUserMetas(city, phone_code, country, fire_base_token)
    //                                val subscription_str = userObject.getString("subscription")
    //                                val subscriptionObject = JSONObject(subscription_str)
    //                                val courses = subscriptionObject.getString("courses")
    //                                val consultations = subscriptionObject.getString("consultations")
    //                                val current_package = subscriptionObject.getString("package")
    //                                val subscription = DataSubscription(courses, consultations, current_package)
    //                                val dataUser = DataUser(id, fname, lname, email, phone, image, member_since, user_metas, dataUserObject.access_token, subscription)
    //                                preferencesHelper.logInUser = dataUser
    //                                val message = jsonObject.getString("message")
    //                                helperMethods.showToastMessage(message)
    //                                GlobalData.profileUpdate = true
    //                                setUserDetils()
    //                                val hashMap = hashMapOf<String, Any>()
    //                                hashMap.put("user_id", id)
    //                                hashMap.put("fname", fname)
    //                                hashMap.put("lname", lname)
    //                                hashMap.put("email", email)
    //                                hashMap.put("phone", phone)
    //                                hashMap.put("image", image)
    //                                hashMap.put("fire_base_token", fire_base_token)
    //                                hashMap.put("user_type", "user")
    //                                hashMap.put("online_status", true)
    //                                hashMap.put("last_seen", FieldValue.serverTimestamp())
    //                                hashMap.put("availability", true)
    //                                hashMap.put("channel_unique_id", "")
    //                                helperMethods.setUserDetailsToFirestore(id, hashMap)
    //                            } else {
    //                                val message = jsonObject.getString("message")
    //                                helperMethods.AlertPopup("Alert", message)
    //                            }
    //                        } catch (e: JSONException) {
    //                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
    //                            e.printStackTrace()
    //                        } catch (e: IOException) {
    //                            e.printStackTrace()
    //                        }
    //                    } else {
    //                        Log.d("body", "Body Empty")
    //                    }
    //                } else {
    //                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
    //                    Log.d("body", "Not Successful")
    //                }
    //            }
    //
    //            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
    //                helperMethods.dismissProgressDialog()
    //                t.printStackTrace()
    //                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
    //            }
    //        })
    //    }
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}
