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
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import digital.upbeat.estisharati_user.DataClassHelper.DataTextsAndColors
import digital.upbeat.estisharati_user.DataClassHelper.DataUserMessageFireStore
import digital.upbeat.estisharati_user.R
import digital.upbeat.estisharati_user.Utils.alertActionClickListner
import kotlinx.android.synthetic.main.alert_popup.view.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class HelperMethods(val context: Context) {
    val calendarInstance: Calendar
    var dialog: android.app.AlertDialog? = null
    val preferencesHelper: SharedPreferencesHelper
    val firebaseFirestore: FirebaseFirestore

    init {
        preferencesHelper = SharedPreferencesHelper(context)
        calendarInstance = Calendar.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
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

    fun isValidPassword(password: String): Boolean {
        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_]).{8,})"
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

    fun showKeyboard(mEtSearch: EditText) {
        mEtSearch.requestFocus()
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun hideSoftKeyboard(mEtSearch: EditText) {
        mEtSearch.clearFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEtSearch.windowToken, 0)
    }

    val profileRequestOption: RequestOptions
        get() {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_img_no_avatar)
            requestOptions.error(R.drawable.ic_img_no_avatar)
            return requestOptions
        }

    fun showProgressDialog(msg: String) {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        val popup_view = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null)
        val aleatdialog = android.app.AlertDialog.Builder(context)
        val progress_text = popup_view.findViewById<TextView>(R.id.progress_text)
        if (!msg.equals("")) {
            progress_text.text = msg
            progress_text.visibility = View.VISIBLE
        } else {
            progress_text.visibility = View.GONE
        }
        aleatdialog.setView(popup_view)
        aleatdialog.setCancelable(false)
        dialog = aleatdialog.create()
        dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.show()
    }

    fun dismissProgressDialog() {
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

    fun setStatusBarColorLightIcon(activity: Activity, StatusBarColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            val view = window.decorView
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN)
            window.statusBarColor = ContextCompat.getColor(activity, StatusBarColor)
            //            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var flags = view.systemUiVisibility
                flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                view.systemUiVisibility = flags
            }
        }
    }

    fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun selfPermission(activity: Activity?) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), 123)
        }
    }

    fun getColorStringFromArray(datas: ArrayList<DataTextsAndColors>): Spanned {
        val stringBuilder = StringBuffer()
        for (data in datas) {
            stringBuilder.append("<font color='${data.color}'>${data.text}</font>")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_MODE_LEGACY)
        } else {
            return Html.fromHtml(stringBuilder.toString())
        }
    }

    fun getHtmlText(content: String): Spanned {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
        } else {
            return Html.fromHtml(content)
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
            selfPermission(activity)
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

    fun sendPushNotification(title: String, text: String) {
        //        Intent intent=null;
        //        if(BaseIntent!=null||!BaseIntent.equalsIgnoreCase("")){
        //            intent=new Intent(BaseIntent);
        //            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        }else{
        //            intent=new Intent();
        //        }
        val num = System.currentTimeMillis().toInt()
        val CHANNEL_ID = "EstisharatiUser"
        val pendingIntent = PendingIntent.getActivity(context, num, Intent(), PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setTicker(context.getString(R.string.app_name)).setContentTitle(title).setContentText(text).setStyle(NotificationCompat.BigTextStyle().bigText(text)).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setColor(ContextCompat.getColor(context, R.color.orange)).setSmallIcon(R.drawable.ic_logo).setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)).setVibrate(longArrayOf(100, 100, 100, 100, 100)).setChannelId(CHANNEL_ID).setContentIntent(pendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION).build()
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, "Ride Home", importance)
            mChannel.description = "Estisharati Notification Settings"
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes)
            mChannel.enableLights(true)
            mChannel.lightColor = ContextCompat.getColor(context, R.color.orange)
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

    fun setUserDetailsToFirestore(user_id: String, hashMap: HashMap<String, Any>) {
        firebaseFirestore.collection("Users").document(user_id).set(hashMap).addOnSuccessListener {}.addOnFailureListener {
            it.localizedMessage?.let {
                Log.d("firebaseFirestore", it)
            }
        }
    }

    fun updateUserDetailsToFirestore(user_id: String, hashMap: HashMap<String, Any>) {
        firebaseFirestore.collection("Users").document(user_id).update(hashMap).addOnSuccessListener {}.addOnFailureListener {
            it.localizedMessage?.let {
                Log.d("firebaseFirestore", it)
            }
        }
    }

    fun setCallsDetailsToFirestore(id: String, hashMap: HashMap<String, Any>) {
        firebaseFirestore.collection("Calls").document(id).set(hashMap).addOnSuccessListener {}.addOnFailureListener {
            it.localizedMessage?.let {
                Log.d("firebaseFirestore", it)
            }
        }
    }

    fun updateCallsDetailsToFirestore(id: String, hashMap: HashMap<String, Any>) {
        firebaseFirestore.collection("Calls").document(id).update(hashMap).addOnSuccessListener {}.addOnFailureListener {
            it.localizedMessage?.let {
                Log.d("firebaseFirestore", it)
            }
        }
    }

    fun isValidMobile(phone: String): Boolean {
        var check = false
        check = if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length < 9 || phone.length > 13) {
                // if(phone.length() != 10) {
                false
                // txtPhone.setError("Not Valid Number");
            } else {
                Patterns.PHONE.matcher(phone).matches()
            }
        } else {
            false
        }
        return check
    }

    fun getFormattedDate(date: Date?): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = if (date != null) date.time else Date().time
        val now = Calendar.getInstance()
        val timeFormatString = "h:mm aa"
        val dateTimeFormatString = "E, MMMM d, h:mm aa"
        val HOURS = 60 * 60 * 60.toLong()
        return if (now[Calendar.DATE] === smsTime[Calendar.DATE]) {
            "Today " + SimpleDateFormat(timeFormatString).format(smsTime.time)
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] === 1) {
            "Yesterday " + SimpleDateFormat(timeFormatString).format(smsTime.time)
        } else if (now[Calendar.YEAR] === smsTime[Calendar.YEAR]) {
            SimpleDateFormat(dateTimeFormatString).format(smsTime.time).toString()
        } else {
            SimpleDateFormat("MMMM dd yyyy, h:mm aa").format(smsTime.time).toString()
        }
    }

    fun getFormattedTimeInMilis(timeInMilis: Long): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = timeInMilis
        val now = Calendar.getInstance()
        val timeFormatString = "h:mm aa"
        val dateTimeFormatString = "EEEE, MMMM d, h:mm aa"
        val HOURS = 60 * 60 * 60.toLong()
        return if (now[Calendar.DATE] === smsTime[Calendar.DATE]) {
            "Today " + SimpleDateFormat(timeFormatString).format(smsTime.time)
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] === 1) {
            "Yesterday " + SimpleDateFormat(timeFormatString).format(smsTime.time)
        } else if (now[Calendar.YEAR] === smsTime[Calendar.YEAR]) {
            SimpleDateFormat(dateTimeFormatString).format(smsTime.time).toString()
        } else {
            SimpleDateFormat("MMMM dd yyyy, h:mm aa").format(smsTime.time).toString()
        }
    }

    //    fun isvalidMobileNumber(number: String): Boolean {
    //        return try {
    //            if (!number.trim().equals("", ignoreCase = true)) {
    //                if (number.trim().length >= 10 && number.toLong() > 0) {
    //                    true
    //                } else {
    //                    false
    //                }
    //            } else {
    //                false
    //            }
    //        } catch (e: Exception) {
    //            e.printStackTrace()
    //            false
    //        }
    //    }
    val requestOption: RequestOptions
        get() {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.mipmap.ic_launcher)
            requestOptions.error(R.mipmap.ic_launcher)
            requestOptions.fallback(R.mipmap.ic_launcher)
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

    fun containsUserIdForChat(userMessageFireStore: ArrayList<DataUserMessageFireStore>, useId: String): Boolean {
        for (data in userMessageFireStore) {
            if (data.dataUserFireStore.user_id.equals(useId)) {
                return false
            }
        }
        return true
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
        val actionOkBtn = LayoutView.findViewById<TextView>(R.id.actionOkBtn)
        val confirmationLayout = LayoutView.findViewById<LinearLayout>(R.id.confirmationLayout)
        title.text = Title
        message.text = getHtmlText(Message)
        message.movementMethod = ScrollingMovementMethod()
        confirmationLayout.visibility = View.GONE
        actionOkBtn.visibility = View.VISIBLE
        actionOkBtn.setOnClickListener { dialog.dismiss() }
    }

    fun showAlertDialog(context: Context, alertActionClickListner: alertActionClickListner, title: String, message: String, ifAlert: Boolean, okBtn: String, cancelBtn: String) {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.alert_popup, null)
        val aleatdialog = AlertDialog.Builder(context)
        aleatdialog.setView(layoutView)
        aleatdialog.setCancelable(false)
        val dialog = aleatdialog.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        layoutView.title.text = title
        layoutView.message.text = message
        layoutView.actionOkBtn.text = okBtn
        layoutView.actionOk.text = okBtn
        layoutView.actionCancel.text = cancelBtn
        if (ifAlert) {
            layoutView.confirmationLayout.visibility = View.GONE
            layoutView.actionOkBtn.visibility = View.VISIBLE
        } else {
            layoutView.confirmationLayout.visibility = View.VISIBLE
            layoutView.actionOkBtn.visibility = View.GONE
        }

        layoutView.actionOk.setOnClickListener {
            dialog.dismiss()
            alertActionClickListner.onActionOk()
        }
        layoutView.actionOkBtn.setOnClickListener {
            dialog.dismiss()
            alertActionClickListner.onActionOk()
        }
        layoutView.actionCancel.setOnClickListener {
            dialog.dismiss()
            alertActionClickListner.onActionCancel()
        }
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