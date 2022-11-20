package estisharatibussiness.users.com.UserInterfaces

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitApiClient
import estisharatibussiness.users.com.ApiHelperClasses.RetrofitInterface
import estisharatibussiness.users.com.DataClassHelperMehtods.Login.DataUser
import estisharatibussiness.users.com.Helper.GlobalData
import estisharatibussiness.users.com.Helper.HelperMethods
import estisharatibussiness.users.com.Helper.SharedPreferencesHelper
import estisharatibussiness.users.com.R
import estisharatibussiness.users.com.UtilsClasses.BaseCompatActivity
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.activity_contact_us.nav_back
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

class ActivityContactUs : BaseCompatActivity() {
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    lateinit var helperMethods: HelperMethods
    lateinit var dataUserObject: DataUser
    lateinit var retrofitInterface: RetrofitInterface
    var userId = ""
    var userName = ""
    lateinit var pickiT: PickiT
    var uploadFile: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        initViews()
        clickEvents()
        setUserDetails()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@ActivityContactUs)
        sharedPreferencesHelper = SharedPreferencesHelper(this@ActivityContactUs)
        retrofitInterface = RetrofitApiClient(GlobalData.BaseUrl).getRetrofit().create(RetrofitInterface::class.java)
        dataUserObject = sharedPreferencesHelper.logInUser
        intent.getStringExtra("userId")?.let {
            userId = it
        }
        intent.getStringExtra("userName")?.let {
            userName = it
        }
        pickiT = PickiT(this, object : PickiTCallbacks {
            override fun PickiTonUriReturned() {
            }

            override fun PickiTonStartListener() {
            }

            override fun PickiTonProgressUpdate(progress: Int) {
            }

            override fun PickiTonCompleteListener(filePath: String?, wasDriveFile: Boolean, wasUnknownProvider: Boolean, wasSuccessful: Boolean, Reason: String?) {
                if (filePath == null && !wasSuccessful) {
                    Toast.makeText(this@ActivityContactUs, getString(R.string.could_not_get_image), Toast.LENGTH_LONG).show()
                    return
                }
                filePath?.let {
                    Log.d("path", filePath + "")
                    uploadFile = File(filePath)
                    uploadImageView.visibility = View.VISIBLE
                    uploadViolatingLabel.visibility = View.GONE
                    uploadImageView.setImageURI(Uri.fromFile(uploadFile))
                }
            }

            override fun PickiTonMultipleCompleteListener(paths: java.util.ArrayList<String>?, wasSuccessful: Boolean, Reason: String?) {
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

    fun clickEvents() {
        nav_back.setOnClickListener { finish() }

        contactusSend.setOnClickListener {
            if (contactusValidation()) {
                ContactusApiCall(contactusName.toText(), contactusPhone.toText(), contactusEmail.toText(), GlobalData.homeResponse.message_types.get(messageTypeSpinner.selectedItemPosition).id, contactusSubject.toText(), contactusMsg.toText(), userId)
            }
        }
        uploadImage.setOnClickListener {
            helperMethods.ChangeProfilePhotoPopup(this@ActivityContactUs)
        }
    }

    fun setUserDetails() {
        contactusName.text = (dataUserObject.fname + " " + dataUserObject.lname).toEditable()
        contactusEmail.text = dataUserObject.email.toEditable()
        var phone = dataUserObject.phone
        phone = phone.replace(" ", "")
        contactusPhone.text = phone.toEditable()
        val messageTypesArrayList: ArrayList<String> = arrayListOf()
        for (types in GlobalData.homeResponse.message_types) {
            messageTypesArrayList.add(types.title)
        }
        val adapter = ArrayAdapter(this@ActivityContactUs, R.layout.support_simple_spinner_dropdown_item, messageTypesArrayList)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        messageTypeSpinner.adapter = adapter

        if (userName.isNotEmpty()) {
            reportTo.visibility = View.VISIBLE
            reportTo.text = getString(R.string.report_to) + " " + userName
        } else {
            reportTo.visibility = View.GONE
        }
    }

    fun contactusValidation(): Boolean {
        if (contactusName.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_name))
            return false
        }
        if (contactusEmail.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_email_address))
            return false
        }
        if (!helperMethods.isvalidEmail(contactusEmail.toText())) {
            helperMethods.showToastMessage(getString(R.string.enter_valid_email_address))
            return false
        }
        if (contactusPhone.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_phone_number))
            return false
        } //        if (!helperMethods.isValidMobile(contactusPhone.toText())) {
        //            helperMethods.showToastMessage(getString(R.string.enter_valid_phone_number))
        //            return false
        //        }
        if (contactusSubject.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_subject))
            return false
        }
        if (contactusMsg.toText().equals("")) {
            helperMethods.showToastMessage(getString(R.string.enter_your_message))
            return false
        }
        if (!helperMethods.isConnectingToInternet) {
            helperMethods.AlertPopup(getString(R.string.internet_connection_failed), getString(R.string.please_check_your_internet_connection_and_try_again))
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(this@ActivityContactUs, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ActivityContactUs, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@ActivityContactUs, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            helperMethods.ChangeProfilePhotoPopup(this@ActivityContactUs)
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

    fun ContactusApiCall(name: String, phone: String, email: String, messageType: String, subject: String, msg: String, userId: String) {
        val nameRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val phoneRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), phone)
        val emailRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), email)
        val messageTypeRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), messageType)
        val subjectRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), subject)
        val msgRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), msg)
        val userIdRequestBody: RequestBody = RequestBody.create(MediaType.parse("text/plain"), userId)
        var imageFile: MultipartBody.Part? = null
        uploadFile?.let {
            val requestBody = RequestBody.create(MediaType.parse("*/*"), it)
            imageFile = MultipartBody.Part.createFormData("image", it.name, requestBody)
        }
        helperMethods.showProgressDialog(getString(R.string.please_wait_while_loading))
        val responseBodyCall = retrofitInterface.CONTACTUS_API_CALL("Bearer ${dataUserObject.access_token}", nameRequestBody, phoneRequestBody, emailRequestBody, messageTypeRequestBody, subjectRequestBody, msgRequestBody, userIdRequestBody, imageFile)
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
                                finish()
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

    fun EditText.toText(): String = text.toString().trim()
    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
