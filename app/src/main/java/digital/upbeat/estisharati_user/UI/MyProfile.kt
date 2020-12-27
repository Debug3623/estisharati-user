package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.*
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.profile_edit_popup.*
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

class MyProfile : AppCompatActivity() {
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
        setUserDetils()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@MyProfile)
        preferencesHelper = SharedPreferencesHelper(this@MyProfile)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    fun setUserDetils() {
        dataUserObject = preferencesHelper.logInUser
        user_name.text = dataUserObject.fname + " " + dataUserObject.lname
        phone.text = dataUserObject.phone
        courses_count.text = dataUserObject.subscription.courses
        consultations_count.text = dataUserObject.subscription.consultations
        current_package.text = dataUserObject.subscription.package_count
        email_address.text = dataUserObject.email
        Glide.with(this@MyProfile).load(dataUserObject.image).apply(helperMethods.profileRequestOption).into(profile_picture)
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
            helperMethods.ChangeProfilePhotoPopup(this@MyProfile)
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
            val intent = Intent(this@MyProfile, Packages::class.java)
            intent.putExtra("viaFrom", "Home")
            startActivity(intent)
        }
        navMyCourse.setOnClickListener {
            startActivity(Intent(this@MyProfile, MyCourses::class.java))
        }
        navMyConsultations.setOnClickListener {
            startActivity(Intent(this@MyProfile, MyConsultations::class.java))
        }
        navPackages.setOnClickListener {
            startActivity(Intent(this@MyProfile, MyPackages::class.java))
        }
    }

    fun ChangePasswordApiCall(currentPassword: String, newPassword: String, confirmPassword: String) {
        helperMethods.showProgressDialog("Please wait while loading...")
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@MyProfile, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MyProfile, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MyProfile, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            helperMethods.ChangeProfilePhotoPopup(this@MyProfile)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GlobalData.PICK_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val img_uri = data.data
                val filePath = helperMethods.getFilePath(img_uri!!)
                if (filePath == null) {
                    Toast.makeText(this@MyProfile, "Could not get image", Toast.LENGTH_LONG).show()
                    return
                }
                profilePictureUpdateApiCall(filePath)
            }
        } else if (requestCode == GlobalData.PICK_IMAGE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val yourSelectedImage = data.extras!!.get("data") as Bitmap
                val img_uri = helperMethods.getImageUriFromBitmap(yourSelectedImage)
                val filePath = helperMethods.getFilePath(img_uri)
                if (filePath == null) {
                    Toast.makeText(this@MyProfile, "Could not get image", Toast.LENGTH_LONG).show()
                    return
                }
                profilePictureUpdateApiCall(filePath)
            }
        }
    }

    fun profilePictureUpdateApiCall(filePath: String) {
        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val imageFile = MultipartBody.Part.createFormData("image", file.getName(), requestBody)

        helperMethods.showProgressDialog("Profile picture is updating...")
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

    fun profileEditPopup() {
        val countryArrayList = preferencesHelper.countryCity
        LayoutView = LayoutInflater.from(this@MyProfile).inflate(R.layout.profile_edit_popup, null)
        val aleatdialog = AlertDialog.Builder(this@MyProfile)
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
        val typeface = ResourcesCompat.getFont(this@MyProfile, R.font.almarai_regular)
        val countryAdapter = ArrayAdapter(this@MyProfile, R.layout.support_simple_spinner_dropdown_item, countryArrString)
        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        LayoutView.ud_country_spinner.adapter = countryAdapter
        LayoutView.ud_country_spinner.setSelection(countryIndex, true)
        val v2 = LayoutView.ud_country_spinner.selectedView
        (v2 as TextView).textSize = 15f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
        LayoutView.ud_country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                view?.let {
                    (view as TextView).textSize = 15f
                    view.typeface = typeface
                    view.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
                }
                val cityArrString = arrayListOf<String>()
                for (city in countryArrayList.get(position).cities) {
                    cityArrString.add(city.city_name)
                }
                val cityAdapter = ArrayAdapter(this@MyProfile, R.layout.support_simple_spinner_dropdown_item, cityArrString)
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
        val cityAdapter = ArrayAdapter(this@MyProfile, R.layout.support_simple_spinner_dropdown_item, cityArrString)
        cityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        LayoutView.ud_city_spinner.adapter = cityAdapter
        LayoutView.ud_city_spinner.setSelection(cityIndex, true)
        val v3 = LayoutView.ud_city_spinner.selectedView
        (v3 as TextView).textSize = 15f
        v3.typeface = typeface
        v3.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
        LayoutView.ud_city_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                view?.let {
                    (view as TextView).textSize = 15f
                    view.typeface = typeface
                    view.setTextColor(ContextCompat.getColor(this@MyProfile, R.color.black))
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
            helperMethods.showToastMessage("Enter first name")
            return false
        }
        if (LayoutView.ud_lname.toText().equals("")) {
            helperMethods.showToastMessage("Enter last name")
            return false
        }
        if (LayoutView.ud_email_address.toText().equals("")) {
            helperMethods.showToastMessage("Enter email address")
            return false
        }
        if (!helperMethods.isvalidEmail(LayoutView.ud_email_address.toText())) {
            helperMethods.showToastMessage("Enter valid email address")
            return false
        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun countryCityApiCall() {
        helperMethods.showProgressDialog("Please wait while Loading...")
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

    fun profileUpdateApiCall(fNmae: String, lName: String, emailAddress: String, countryID: String, cityID: String) {
        helperMethods.showProgressDialog("Profile is updating...")
        val responseBodyCall = retrofitInterface.PROFILE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", fNmae, lName, emailAddress, countryID, cityID)
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

    fun validateUpdatePassword(): Boolean {
        if (currentPassword.toText().equals("")) {
            helperMethods.showToastMessage("Enter your current password !")
            return false
        }
        if (newPassword.toText().equals("")) {
            helperMethods.showToastMessage("Enter your new password !")
            return false
        }
        if (!helperMethods.isValidPassword(newPassword.toText())) {
            helperMethods.AlertPopup("Alert", "Password at least 8 characters including a lower-case letter, an upper–case letter, a number and one special character")
            return false
        }
        if (confirmPassword.toText().equals("")) {
            helperMethods.showToastMessage("Enter your confirm password !")
            return false
        }
        if (!newPassword.toText().equals(confirmPassword.toText())) {
            helperMethods.showToastMessage("New password and Confirm password not same !")
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
