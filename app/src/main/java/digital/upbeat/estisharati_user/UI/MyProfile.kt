package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_user.ApiHelper.RetrofitApiClient
import digital.upbeat.estisharati_user.ApiHelper.RetrofitInterface
import digital.upbeat.estisharati_user.DataClassHelper.DataSubscription
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import digital.upbeat.estisharati_user.DataClassHelper.DataUserMetas
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.Helper.SharedPreferencesHelper
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_my_profile.*
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

class MyProfile : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    lateinit var preferencesHelper: SharedPreferencesHelper
    lateinit var retrofitInterface: RetrofitInterface
    lateinit var dataUserObject: DataUser
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
        dataUserObject = preferencesHelper.getLogInUser()
        user_name.text = dataUserObject.fname + " " + dataUserObject.lname
        courses_count.text = dataUserObject.subscription.courses
        consultations_count.text = dataUserObject.subscription.consultations
        current_package.text = dataUserObject.subscription.current_package
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
            }
        }
        profile_picture_layout.setOnClickListener {
            helperMethods.ChangeProfilePhotoPopup(this@MyProfile)
        }
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
        val first_name = RequestBody.create(MediaType.parse("text/plain"), dataUserObject.fname)


        val file = File(filePath)
        val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
        val image = MultipartBody.Part.createFormData("image", file.getName(), requestBody)

        helperMethods.showProgressDialog("Profile picture is updating...")
        val responseBodyCall = retrofitInterface.PROFILE_PICTURE_UPDATE_API_CALL("Bearer ${dataUserObject.access_token}", image)
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
                                val id = userObject.getString("id")
                                val fname = userObject.getString("fname")
                                val lname = userObject.getString("lname")
                                val email = userObject.getString("email")
                                val phone = userObject.getString("phone")
                                val image = userObject.getString("image")
                                val member_since = userObject.getString("member_since")
                                val user_metasStr = userObject.getString("user_metas")
                                val userMetasObject = JSONObject(user_metasStr)
                                val city = userMetasObject.getString("city")
                                val contact = userMetasObject.getString("contact")
                                val user_metas = DataUserMetas(city, contact)
                                val subscription_str = userObject.getString("subscription")
                                val subscriptionObject = JSONObject(subscription_str)
                                val courses = subscriptionObject.getString("courses")
                                val consultations = subscriptionObject.getString("consultations")
                                val current_package = subscriptionObject.getString("package")
                                val subscription = DataSubscription(courses, consultations, current_package)
                                val dataUser = DataUser(id, fname, lname, email, phone, image, member_since, user_metas, dataUserObject.access_token, subscription)
                                preferencesHelper.setLogInUser(dataUser)
                                val message = jsonObject.getString("message")
                                helperMethods.showToastMessage(message)
                                GlobalData.profileUpdate=true
                                setUserDetils()
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
