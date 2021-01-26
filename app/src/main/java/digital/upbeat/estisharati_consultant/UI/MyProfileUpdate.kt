package digital.upbeat.estisharati_consultant.UI

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.DataClassHelper.CountryCity.DataCountry
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_consultant.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_consultant.DataClassHelper.Login.DataUser
import digital.upbeat.estisharati_consultant.Helper.GlobalData
import digital.upbeat.estisharati_consultant.Utils.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_my_profile_update.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class MyProfileUpdate : BaseCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUserObject: DataUser
    var countryArrayList = arrayListOf<DataCountry>()
    var uploadType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile_update)

        initViews()
        clickEvents()
        setUserDetils()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@MyProfileUpdate)
        preferencesHelper = SharedPreferencesHelper(this@MyProfileUpdate)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
    }

    fun setUserDetils() {
        dataUserObject = preferencesHelper.logInConsultant
        countryArrayList = preferencesHelper.countryCity
        Glide.with(this@MyProfileUpdate).load(dataUserObject.image).apply(helperMethods.profileRequestOption).into(profile_picture)

        ud_fname.text = dataUserObject.fname.toEditable()
        ud_lname.text = dataUserObject.lname.toEditable()
        val phone = dataUserObject.phone.replace(dataUserObject.user_metas.phone_code, "")
        if (!dataUserObject.user_metas.phone_code.equals("")) {
            ud_phone_codePicker.setCountryForPhoneCode(dataUserObject.user_metas.phone_code.toInt())
        }
        ud_phone.text = phone.toEditable()
        ud_email_address.text = dataUserObject.email.toEditable()
        ud_qualification_brief.text = dataUserObject.user_metas.qualification_brief.toEditable()
        qualification_name.text = helperMethods.getFileNameFromURL(dataUserObject.user_metas.qualification)
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
        ud_country_spinner.onItemSelectedListener = null
        val typeface = ResourcesCompat.getFont(this@MyProfileUpdate, R.font.almarai_regular)
        val countryAdapter = ArrayAdapter(this@MyProfileUpdate, R.layout.support_simple_spinner_dropdown_item, countryArrString)
        countryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        ud_country_spinner.adapter = countryAdapter
        ud_country_spinner.setSelection(countryIndex, true)
        val v2 = ud_country_spinner.selectedView
        (v2 as TextView).textSize = 15f
        v2.typeface = typeface
        v2.setTextColor(ContextCompat.getColor(this@MyProfileUpdate, R.color.black))
        ud_country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0 && view != null) {
                    (view as TextView).textSize = 15f
                    view.typeface = typeface
                    view.setTextColor(ContextCompat.getColor(this@MyProfileUpdate, R.color.black))
                    val cityArrString = arrayListOf<String>()
                    for (city in countryArrayList.get(position).cities) {
                        cityArrString.add(city.city_name)
                    }
                    val cityAdapter = ArrayAdapter(this@MyProfileUpdate, R.layout.support_simple_spinner_dropdown_item, cityArrString)
                    cityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                    ud_city_spinner.adapter = cityAdapter
                }
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
        val cityAdapter = ArrayAdapter(this@MyProfileUpdate, R.layout.support_simple_spinner_dropdown_item, cityArrString)
        cityAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        ud_city_spinner.adapter = cityAdapter
        ud_city_spinner.setSelection(cityIndex, true)
        val v3 = ud_city_spinner.selectedView
        (v3 as TextView).textSize = 15f
        v3.typeface = typeface
        v3.setTextColor(ContextCompat.getColor(this@MyProfileUpdate, R.color.black))
        ud_city_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position >= 0 && view != null) {
                    (view as TextView).textSize = 15f
                    view.typeface = typeface
                    view.setTextColor(ContextCompat.getColor(this@MyProfileUpdate, R.color.black))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        /*******Spinner end***********/
    }

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }

        profile_picture_layout.setOnClickListener {
            uploadType = "profilePicture"
            helperMethods.ChangeProfilePhotoPopup(this@MyProfileUpdate)
        }
        add_qualification.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MyProfileUpdate, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MyProfileUpdate, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MyProfileUpdate, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                requestQualification()
            } else {
                helperMethods.selfPermission(this@MyProfileUpdate)
            }
        }
        btn_update.setOnClickListener {
            if (updateValidation()) {
                profileUpdateApiCall(ud_fname.toText(), ud_lname.toText(), ud_phone_codePicker.selectedCountryCodeWithPlus + "" + ud_phone.toText(), ud_phone_codePicker.selectedCountryCodeWithPlus, ud_email_address.toText(), ud_qualification_brief.toText(), countryArrayList.get(ud_country_spinner.selectedItemPosition).country_id, countryArrayList.get(ud_country_spinner.selectedItemPosition).cities.get(ud_city_spinner.selectedItemPosition).city_id)
            }
        }
    }

    fun updateValidation(): Boolean {
        if (ud_fname.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_first_name))
            return false
        }
        if (ud_lname.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_last_name))
            return false
        }
        if (ud_phone.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_phone_number))
            return false
        }
        if (!helperMethods.isValidMobile(ud_phone_codePicker.selectedCountryCodeWithPlus + "" + ud_phone.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_phone_number))
            return false
        }
        if (ud_email_address.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_email_address))
            return false
        }
        if (!helperMethods.isvalidEmail(ud_email_address.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_email_address))
            return false
        }
        if (ud_qualification_brief.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_qualification))
            return false
        }

        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    fun requestQualification() {
        uploadType = "qualification"
        //        val intent = helperMethods.getCustomFileChooserIntent(GlobalData.PDF, GlobalData.DOC, GlobalData.DOCX, GlobalData.CSV)
        val intent = Intent()
        intent.type = "application/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select picture"), GlobalData.PICK_IMAGE_GALLERY)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@MyProfileUpdate, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MyProfileUpdate, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MyProfileUpdate, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (uploadType.equals("profilePicture")) {
                helperMethods.ChangeProfilePhotoPopup(this@MyProfileUpdate)
            } else if (uploadType.equals("qualification")) {
                requestQualification()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GlobalData.PICK_IMAGE_GALLERY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val img_uri = data.data
                val filePath = helperMethods.getFilePath(img_uri!!)
                if (filePath == null) {
                    Toast.makeText(this@MyProfileUpdate, getString(R.string.could_not_get_image), Toast.LENGTH_LONG).show()
                    return
                }
                if (uploadType.equals("profilePicture")) {
                    profilePictureUpdateApiCall(filePath)
                } else if (uploadType.equals("qualification")) {
                    documentUpdateApiCall(filePath)
                }
            }
        } else if (requestCode == GlobalData.PICK_IMAGE_CAMERA) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val yourSelectedImage = data.extras!!.get("data") as Bitmap
                val img_uri = helperMethods.getImageUriFromBitmap(yourSelectedImage)
                val filePath = helperMethods.getFilePath(img_uri)
                if (filePath == null) {
                    Toast.makeText(this@MyProfileUpdate, getString(R.string.could_not_get_image), Toast.LENGTH_LONG).show()
                    return
                }
                if (uploadType.equals("profilePicture")) {
                    profilePictureUpdateApiCall(filePath)
                } else if (uploadType.equals("qualification")) {
                    documentUpdateApiCall(filePath)
                }
            }
        }
    }

    fun profileUpdateApiCall(fNmae: String, lName: String, phone_str: String, phone_code_str: String, email_str: String, qualification_brief_str: String, countryID: String, cityID: String) {
        helperMethods.showProgressDialog(getString(R.string.profile_is_updating))
        val responseBodyCall = retrofitInterface.PROFILE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", fNmae, lName, phone_str, phone_code_str, email_str, qualification_brief_str, countryID, cityID)
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
                                val hashMap = hashMapOf<String, Any>()
                                hashMap.put("user_id", dataUser.id)
                                hashMap.put("fname", dataUser.fname)
                                hashMap.put("lname", dataUser.lname)
                                hashMap.put("email", dataUser.email)
                                hashMap.put("phone", dataUser.phone)
                                hashMap.put("image", dataUser.image)
                                hashMap.put("fire_base_token", dataUser.user_metas.fire_base_token)
                                hashMap.put("user_type", "consultant")
                                hashMap.put("online_status", true)
                                hashMap.put("last_seen", FieldValue.serverTimestamp())
                                hashMap.put("availability", true)
                                hashMap.put("channel_unique_id", "")
                                helperMethods.setUserDetailsToFirestore(dataUser.id, hashMap)
                                preferencesHelper.logInConsultant = dataUser
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                                GlobalData.profileUpdate = true
                                setUserDetils()
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

    fun profilePictureUpdateApiCall(filePath: String) {
        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val imageFile = MultipartBody.Part.createFormData("image", file.getName(), requestBody)

        helperMethods.showProgressDialog(getString(R.string.profile_picture_is_updating))
        val responseBodyCall = retrofitInterface.FILE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", imageFile)
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
                                preferencesHelper.logInConsultant = dataUser
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

    fun documentUpdateApiCall(filePath: String) {
        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val imageFile = MultipartBody.Part.createFormData("qualification", file.getName(), requestBody)
        helperMethods.showProgressDialog(getString(R.string.qualification_is_updating))
        val responseBodyCall = retrofitInterface.FILE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", imageFile)
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
                                val user_metasStr = userObject.getString("user_metas")
                                val userMetasObject = JSONObject(user_metasStr)
                                val qualification = userMetasObject.getString("qualification")
                                val dataUser = dataUserObject
                                dataUser.user_metas.qualification = qualification
                                preferencesHelper.logInConsultant = dataUser
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                                GlobalData.profileUpdate = true
                                setUserDetils()
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

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    fun EditText.toText(): String = text.toString().trim()
}