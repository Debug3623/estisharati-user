package estisharatibussiness.users.com.UserInterfaces

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.CityCountry.DataCity
import estisharatibussiness.users.com.DataClassHelperMehtods.CityCountry.DataCountry
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.profile_edit_popup.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.ArrayList

class ActivityMyProfile : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUserObject: DataUser
    lateinit var LayoutView: View
    lateinit var pickiT: PickiT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        initViews()
        clickEvents()
        setUserDetils()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityMyProfile)
        preferencesHelper = SharedPreferencesHelper(this@ActivityMyProfile)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        pickiT = PickiT(this, object : PickiTCallbacks {
            override fun PickiTonUriReturned() {
            }

            override fun PickiTonStartListener() {
            }

            override fun PickiTonProgressUpdate(progress: Int) {
            }

            override fun PickiTonCompleteListener(filePath: String?, wasDriveFile: Boolean, wasUnknownProvider: Boolean, wasSuccessful: Boolean, Reason: String?) {
                if (filePath == null && !wasSuccessful) {
                    Toast.makeText(this@ActivityMyProfile, getString(R.string.could_not_get_image), Toast.LENGTH_LONG).show()
                    return
                }
                filePath?.let {
                    Log.d("path", filePath + "")
                    profilePictureUpdateApiCall(filePath)
                }
            }

            override fun PickiTonMultipleCompleteListener(paths: ArrayList<String>?, wasSuccessful: Boolean, Reason: String?) {
            }
        }, this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        pickiT.deleteTemporaryFile(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        pickiT.deleteTemporaryFile(this)
    }

    fun setUserDetils() {
        dataUserObject = preferencesHelper.logInUser
        user_name.text = dataUserObject.fname + " " + dataUserObject.lname
        phone.text = dataUserObject.phone
        courses_count.text = dataUserObject.subscription.courses
        consultations_count.text = dataUserObject.subscription.consultations
        current_package.text = dataUserObject.subscription.package_count
        email_address.text = dataUserObject.email
        Glide.with(this@ActivityMyProfile).load(dataUserObject.image).apply(helperMethods.profileRequestOption).into(profile_picture)

        if (dataUserObject.google_login) {
            phoneNumberLayout.visibility = View.GONE
            passwordLayout.visibility = View.GONE
        } else {
            phoneNumberLayout.visibility = View.VISIBLE
            passwordLayout.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU) fun clickEvents() {
        nav_back.setOnClickListener { finish() }
        change_password_layot.visibility = View.GONE
        change_password_arrow.setImageResource(R.drawable.ic_up_arrow_white)
        password_btn.setOnClickListener {
            if (change_password_layot.visibility == View.VISIBLE) {
                change_password_layot.visibility = View.GONE
                change_password_arrow.setImageResource(R.drawable.ic_up_arrow_white)
            } else {
                currentPassword.text = "".toEditable()
                newPassword.text = "".toEditable()
                confirmPassword.text = "".toEditable()
                change_password_layot.visibility = View.VISIBLE
                change_password_arrow.setImageResource(R.drawable.ic_down_arrow_white)
                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }
        profile_picture_layout.setOnClickListener {
            helperMethods.ChangeProfilePhotoPopup(this@ActivityMyProfile)
        }
        edit_profile.setOnClickListener {
            if (helperMethods.isConnectingToInternet) {
                if (preferencesHelper.countryCity.size > 0) {
                    profileEditPopup()
                } else {
                    countryCityApiCall()
                }
            } else {
                helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            }
        }
        savePassword.setOnClickListener {
            if (validateUpdatePassword()) {
                ChangePasswordApiCall(currentPassword.toText(), newPassword.toText(), confirmPassword.toText())
            }
        }
        upgradePackage.setOnClickListener {
            val intent = Intent(this@ActivityMyProfile, ActivityPackages::class.java)
            intent.putExtra("viaFrom", "Home")
            startActivity(intent)
        }
        navMyCourse.setOnClickListener {
            startActivity(Intent(this@ActivityMyProfile, ActivityMyCourses::class.java))
        }
        navMyConsultations.setOnClickListener {
            startActivity(Intent(this@ActivityMyProfile, ActivityMyConsultations::class.java))
        }
        navPackages.setOnClickListener {
            startActivity(Intent(this@ActivityMyProfile, ActivityMyPackages::class.java))
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@ActivityMyProfile, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ActivityMyProfile, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ActivityMyProfile, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            helperMethods.ChangeProfilePhotoPopup(this@ActivityMyProfile)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GlobalData.PICK_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                pickiT.getPath(data.data, Build.VERSION.SDK_INT)
            }
        } else if (requestCode == GlobalData.PICK_IMAGE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val yourSelectedImage = data.extras!!.get("data") as Bitmap
                val img_uri = helperMethods.getImageUriFromBitmap(yourSelectedImage)
                pickiT.getPath(img_uri, Build.VERSION.SDK_INT)
            }
        }
    }

    fun profilePictureUpdateApiCall(filePath: String) {
        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val imageFile = MultipartBody.Part.createFormData("image", file.name, requestBody)

        helperMethods.showProgressDialog(getString(R.string.profile_picture_is_updating))
        val responseBodyCall = retrofitInterface.PROFILE_PICTURE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", imageFile)
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val jsonObject = JSONObject(response.body()!!.string())
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val userString = jsonObject.getString("user")
                                val userObject = JSONObject(userString)
                                val image = userObject.getString("image")
                                val dataUser = dataUserObject
                                dataUser.image = image
                                preferencesHelper.logInUser = dataUser
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                                GlobalData.profileUpdate = true
                                setUserDetils()
                                val hashMap = hashMapOf<String, Any>()
                                hashMap.put("image", image)
                                helperMethods.updateUserDetailsToFirestore(dataUser.id, hashMap)
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

    fun profileEditPopup() {
        val countryArrayList = preferencesHelper.countryCity
        LayoutView = LayoutInflater.from(this@ActivityMyProfile).inflate(R.layout.profile_edit_popup, null)
        val aleatdialog = AlertDialog.Builder(this@ActivityMyProfile)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        LayoutView.ud_fname.text = dataUserObject.fname.toEditable()
        LayoutView.ud_lname.text = dataUserObject.lname.toEditable()
        LayoutView.ud_email_address.text = dataUserObject.email.toEditable()
        /*******Spinner start***********/
        var countryIndex = 0
        var cityIndex = 0
        val countryArrString = arrayListOf<String>()
        for (index in countryArrayList.indices) {
            countryArrString.add(countryArrayList.get(index).country_name)
            if (countryArrayList.get(index).country_id.equals(dataUserObject.user_metas.country)) {
                countryIndex = index
            }
        }
        val typeface = ResourcesCompat.getFont(this@ActivityMyProfile, R.font.almarai_regular)
        val countryAdapter = ArrayAdapter(this@ActivityMyProfile, R.layout.support_simple_spinner_dropdown_item, countryArrString)
        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        LayoutView.ud_country_spinner.adapter = countryAdapter
        LayoutView.ud_country_spinner.setSelection(countryIndex, true)
        val v2 = LayoutView.ud_country_spinner.selectedView
        (v2 as TextView).textSize = 15f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@ActivityMyProfile, R.color.black))
        LayoutView.ud_country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                view?.let {
                    (view as TextView).textSize = 15f
                    view.typeface = typeface
                    view.setTextColor(ContextCompat.getColor(this@ActivityMyProfile, R.color.black))
                }
                val cityArrString = arrayListOf<String>()
                for (city in countryArrayList.get(position).cities) {
                    cityArrString.add(city.city_name)
                }
                val cityAdapter = ArrayAdapter(this@ActivityMyProfile, R.layout.support_simple_spinner_dropdown_item, cityArrString)
                cityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                LayoutView.ud_city_spinner.adapter = cityAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        val cityArrString = arrayListOf<String>()
        for (index in countryArrayList.get(countryIndex).cities.indices) {
            cityArrString.add(countryArrayList.get(countryIndex).cities.get(index).city_name)
            if (countryArrayList.get(countryIndex).cities.get(index).city_id.equals(dataUserObject.user_metas.city)) {
                cityIndex = index
            }
        }
        val cityAdapter = ArrayAdapter(this@ActivityMyProfile, R.layout.support_simple_spinner_dropdown_item, cityArrString)
        cityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        LayoutView.ud_city_spinner.adapter = cityAdapter
        LayoutView.ud_city_spinner.setSelection(cityIndex, true)
        val v3 = LayoutView.ud_city_spinner.selectedView
        (v3 as TextView).textSize = 15f
        v3.typeface = typeface
        v3.setTextColor(ContextCompat.getColor(this@ActivityMyProfile, R.color.black))
        LayoutView.ud_city_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                view?.let {
                    (view as TextView).textSize = 15f
                    view.typeface = typeface
                    view.setTextColor(ContextCompat.getColor(this@ActivityMyProfile, R.color.black))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        /*******Spinner end***********/
        LayoutView.cancel.setOnClickListener {
            dialog.dismiss()
        }
        LayoutView.update.setOnClickListener {
            if (updateProfileValidation()) {
                dialog.dismiss()
                profileUpdateApiCall(LayoutView.ud_fname.toText(), LayoutView.ud_lname.toText(), LayoutView.ud_email_address.toText(), countryArrayList.get(LayoutView.ud_country_spinner.selectedItemPosition).country_id, countryArrayList.get(LayoutView.ud_country_spinner.selectedItemPosition).cities.get(LayoutView.ud_city_spinner.selectedItemPosition).city_id)
            }
        }
    }

    fun updateProfileValidation(): Boolean {
        if (LayoutView.ud_fname.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_first_name))
            return false
        }
        if (LayoutView.ud_lname.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_last_name))
            return false
        }
        if (LayoutView.ud_email_address.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_email_address))
            return false
        }
        if (!helperMethods.isvalidEmail(LayoutView.ud_email_address.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_email_address))
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
                                profileEditPopup()
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

    fun profileUpdateApiCall(fNmae: String, lName: String, emailAddress: String, countryID: String, cityID: String) {
        helperMethods.showProgressDialog(getString(R.string.profile_is_updating))
        val responseBodyCall = retrofitInterface.PROFILE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", fNmae, lName, emailAddress, countryID, cityID, "User")
        responseBodyCall.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                helperMethods.dismissProgressDialog()

                if (response.isSuccessful) {
                    if (response.body() != null) {
                        try {
                            val responseString = response.body()!!.string()
                            Log.d("response", response.body()!!.string())
                            val jsonObject = JSONObject(responseString)
                            val status = jsonObject.getString("status")
                            if (status.equals("200")) {
                                val userString = jsonObject.getString("user")
                                val dataUser = Gson().fromJson(userString, DataUser::class.java)
                                preferencesHelper.logInUser = dataUser
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                                GlobalData.profileUpdate = true
                                setUserDetils()
                                val hashMap = hashMapOf<String, Any>()
                                hashMap.put("user_id", dataUser.id)
                                hashMap.put("fname", dataUser.fname)
                                hashMap.put("lname", dataUser.lname)
                                hashMap.put("email", dataUser.email)
                                hashMap.put("phone", dataUser.phone)
                                hashMap.put("image", dataUser.image)
                                hashMap.put("fire_base_token", dataUser.user_metas.fire_base_token)
                                hashMap.put("user_type", "user")
                                hashMap.put("online_status", true)
                                hashMap.put("last_seen", FieldValue.serverTimestamp())
                                hashMap.put("availability", true)
                                hashMap.put("channel_unique_id", "")
                                helperMethods.setUserDetailsToFirestore(dataUser.id, hashMap)
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
            helperMethods.showToastMessage(getString(R.string.enter_your_new_password))
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

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}
