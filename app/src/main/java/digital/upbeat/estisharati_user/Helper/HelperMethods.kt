package digital.upbeat.estisharati_user.Helper

import android.Manifest
import android.annotation.TargetApi
import android.app.*
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Html
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.RequestOptions
import digital.upbeat.estisharati_user.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class HelperMethods(val context: Context) {
    val calendarInstance: Calendar
    var dialog: android.app.AlertDialog? = null
    val preferencesHelper: SharedPreferencesHelper

    init {
        preferencesHelper = SharedPreferencesHelper(context)
        calendarInstance = Calendar.getInstance()
    }

    fun ShowDatePickerDialog(date_picker_text: TextView) {
        DatePickerDialog(context, OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate[year, monthOfYear] = dayOfMonth
            val targetFormat = SimpleDateFormat("MM-dd-yyyy")
            date_picker_text.text = targetFormat.format(newDate.time)
        }, calendarInstance!![Calendar.YEAR], calendarInstance!![Calendar.MONTH], calendarInstance!![Calendar.DAY_OF_MONTH]).show()
    }

    fun ShowTimePickerDialog(time_picker_text: TextView) {
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
        val minute = mcurrentTime[Calendar.MINUTE]
        val SelectedTime = Calendar.getInstance()
        val mTimePicker = TimePickerDialog(context, OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
            SelectedTime[Calendar.HOUR_OF_DAY] = selectedHour
            SelectedTime[Calendar.MINUTE] = selectedMinute
            time_picker_text.text = SimpleDateFormat("hh:mm a").format(SelectedTime.time)
        }, hour, minute, false) //Yes 24 hour time
        mTimePicker.setTitle("Select time")
        mTimePicker.show()
    }

    fun isValidPassword(password: String?): Boolean {
        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-zA-Z])(?=.*[-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_]).{6,})"
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        return pattern.matcher(password).matches()
    }

    fun ShowDateTimePicker(datetime: TextView) {
        val currentDate = Calendar.getInstance()
        val dateTime = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(context, OnDateSetListener { datePicker, year, monthOfYear, dayOfMonth ->
            dateTime[year, monthOfYear] = dayOfMonth
            TimePickerDialog(context, OnTimeSetListener { view, hourOfDay, minute ->
                dateTime[Calendar.HOUR_OF_DAY] = hourOfDay
                dateTime[Calendar.MINUTE] = minute
                val afterOneHour = Calendar.getInstance()
                afterOneHour.add(Calendar.HOUR_OF_DAY, 1)
                if (dateTime.timeInMillis > afterOneHour.timeInMillis) {
                    datetime.text = SimpleDateFormat("dd-MMM-yyyy hh:mm a").format(dateTime.time)
                } else {
                    datetime.text = ""
                    Toast.makeText(context, "Sorry, Your booking time should be 1 hour ahead of current time!", Toast.LENGTH_LONG).show()
                }
            }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], false).show()
        }, currentDate[Calendar.YEAR], currentDate[Calendar.MONTH], currentDate[Calendar.DATE])
        //        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show()
    }

    fun hideSoftKeyboard(input: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }

    fun ShowProgressDialog(msg: String) {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        val popup_view = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null)
        val aleatdialog = android.app.AlertDialog.Builder(context)
        val progress_text = popup_view.findViewById<View>(R.id.progress_text) as TextView
        if (!msg.trim { it <= ' ' }.equals("", ignoreCase = true)) {
            progress_text.text = msg
        } else {
            progress_text.visibility = View.GONE
        }
        aleatdialog.setView(popup_view)
        aleatdialog.setCancelable(false)
        dialog = aleatdialog.create()
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
    }

    fun DismissProgressDialog() {
        try {
            if (dialog != null) {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setStatusBarColor(activity: Activity, StatusBarColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val view = window.decorView
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN)
            window.statusBarColor = ContextCompat.getColor(activity, StatusBarColor)
            //            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                view.systemUiVisibility = flags
            }
        }
    }

    fun SelfPermission(activity: Activity?) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 123)
        }
    }

    fun getColorString(text: String, color: Int): Spanned {
        val bodyData = "<font color='$color'>$text</font>"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(bodyData, Html.FROM_HTML_MODE_LEGACY)
        } else {
            return Html.fromHtml(bodyData)
        }
    }

    fun ChangeProfilePhotoPopup(activity: Activity) {
        if (!isConnectingToInternet) {
            AlertPopup(context.getString(R.string.internet_connection_failed), context.getString(R.string.please_check_your_internet_connection_and_try_again))
            return
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val file_upload_camera_gallery = LayoutInflater.from(context).inflate(R.layout.pick_picture_gallery_camera, null)
            val aleatdialog = AlertDialog.Builder(context)
            aleatdialog.setView(file_upload_camera_gallery)
            aleatdialog.setCancelable(true)
            val dialog = aleatdialog.create()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val upload_from_camera = file_upload_camera_gallery.findViewById<LinearLayout>(R.id.upload_from_camera)
            val upload_from_gallery = file_upload_camera_gallery.findViewById<LinearLayout>(R.id.upload_from_gallery)
            upload_from_gallery.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activity.startActivityForResult(Intent.createChooser(intent, "Select picture"), GlobalData.PICK_IMAGE_GALLERY)
                dialog.dismiss()
            }
            upload_from_camera.setOnClickListener {
                val intent = Intent()
                intent.action = MediaStore.ACTION_IMAGE_CAPTURE
                activity.startActivityForResult(Intent.createChooser(intent, "Select picture"), GlobalData.PICK_IMAGE_CAMERA)
                dialog.dismiss()
            }
        } else {
            SelfPermission(activity)
        }
    }

    fun ActionDialIntent(number: String) {
        val i = Intent(Intent.ACTION_DIAL)
        val p = "tel:$number"
        i.data = Uri.parse(p)
        context.startActivity(i)
    }

    fun MillisUntilToTime(millisUntilFinished: Long): String {
        val minutes = millisUntilFinished / 1000 / 60
        val seconds = (millisUntilFinished / 1000 % 60).toInt()
        Log.d("timier", "seconds remaining: " + DecimalFormat("00").format(minutes) + "." + DecimalFormat("00").format(seconds.toLong()))
        return DecimalFormat("00").format(minutes) + "." + DecimalFormat("00").format(seconds.toLong())
    }

    fun SendPushNotification(title: String?, text: String?) {
        //        Intent intent=null;
        //        if(BaseIntent!=null||!BaseIntent.equalsIgnoreCase("")){
        //            intent=new Intent(BaseIntent);
        //            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        }else{
        //            intent=new Intent();
        //        }
        val num = System.currentTimeMillis().toInt()
        val CHANNEL_ID = "RideHomeUser"
        val pendingIntent = PendingIntent.getActivity(context, num, Intent(), PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setTicker(context.getString(R.string.app_name)).setContentTitle(title).setContentText(text).setStyle(NotificationCompat.BigTextStyle().bigText(text)).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setColor(ContextCompat.getColor(context, R.color.yellow)).setSmallIcon(R.drawable.ic_logo).setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)).setVibrate(longArrayOf(100, 100, 100, 100, 100)).setChannelId(CHANNEL_ID).setContentIntent(pendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, "Ride Home", importance)
            mChannel.description = "Ride Home Notification Settings"
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            mChannel.enableLights(true)
            mChannel.lightColor = ContextCompat.getColor(context, R.color.yellow)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 100, 100, 100, 100)
            mChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(mChannel)
        }
        val appNoti = notification.build()
        appNoti.flags = Notification.FLAG_AUTO_CANCEL
        notificationManager.notify(num, appNoti)
    }

    fun isvalidEmail(email: String): Boolean {
        return if (!email.trim().equals("", ignoreCase = true)) {
            email.matches(Regex("^(.+)@(.+)$"))
        } else {
            false
        }
    }

    fun isvalidMobileNumber(number: String): Boolean {
        return try {
            if (!number.trim().equals("", ignoreCase = true)) {
                if (number.trim().length >= 10 && number.toLong() > 0) {
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    val requestOption: RequestOptions
        get() {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_logo)
            requestOptions.error(R.drawable.ic_logo)
            return requestOptions
        }
    val isConnectingToInternet: Boolean
        get() {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
            return false
        }

    fun getAddressArrFromLatLong(latitude: Double, longitude: Double): List<Address?> {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address?> = ArrayList() // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        return try {
            geocoder.getFromLocation(latitude, longitude, 1).also { addresses = it }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Could not get your Location and check your internet connection", Toast.LENGTH_LONG).show()
            addresses
        }
    }

    fun AlertPopup(Title: String, Message: String) {
        val LayoutView = LayoutInflater.from(context).inflate(R.layout.alert_popup, null)
        val aleatdialog = android.app.AlertDialog.Builder(context)
        aleatdialog.setView(LayoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        val title = LayoutView.findViewById<TextView>(R.id.title)
        val message = LayoutView.findViewById<TextView>(R.id.message)
        val action_ok = LayoutView.findViewById<TextView>(R.id.action_ok)
        title.text = Title
        message.text = Message
        message.movementMethod = ScrollingMovementMethod()
        action_ok.setOnClickListener { dialog.dismiss() }
    }

    fun getImageUriFromBitmap(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getFilePath(uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Unable to get the profile please try different folder", Toast.LENGTH_LONG).show()
            //            ShowCustomToast( "Orofile update failed", "Unable to get the profile please try different folder");
        } finally {
            cursor?.close()
        }
        return null
    }

    companion object {
        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }
    }
}