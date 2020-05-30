package digital.upbeat.estisharati_user.UI

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import digital.upbeat.estisharati_user.Helper.GlobalData
import digital.upbeat.estisharati_user.Helper.HelperMethods
import digital.upbeat.estisharati_user.R
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfile : AppCompatActivity() {
    lateinit var helperMethods: HelperMethods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        initViews()
        clickEvents()
    }

    fun initViews() {
        helperMethods = HelperMethods(this@MyProfile)
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
                profile_picture.setImageURI(img_uri)
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
                          profile_picture.setImageURI(img_uri)

            }
        }
    }
}
