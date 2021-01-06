package digital.upbeat.estisharati_consultant.Helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import digital.upbeat.estisharati_consultant.DataClassHelper.DataCountry
import digital.upbeat.estisharati_consultant.DataClassHelper.DataUser
import org.codehaus.jackson.map.ObjectMapper
import java.io.IOException

class SharedPreferencesHelper(private val mContext: Context) {
    private val PREF_NAME = "estisharati_consultant"
    private val pref_editor: SharedPreferences.Editor
    private val pref: SharedPreferences

    init {
        pref = mContext.getSharedPreferences(PREF_NAME, 0)
        pref_editor = pref.edit()
    }

    var mapper: ObjectMapper? = null
        get() {
            if (field == null) {
                field = ObjectMapper()
            }
            return field
        }
        private set
    var isConsultantLogIn: Boolean
        get() = pref.getBoolean("isConsultantLogIn", false)
        set(IsUserLogIn) {
            pref_editor.putBoolean("isConsultantLogIn", IsUserLogIn).apply()
        }
    var logInConsultant: DataUser
        get() {
            val gson = Gson()
            val json = pref.getString("logInConsultant", "")
            if (json.equals("")) {
                return DataUser()
            } else {
                return gson.fromJson(json, DataUser::class.java)
            }
        }
        set(user) {
            val gson = Gson()
            val json = gson.toJson(user)
            pref_editor.putString("logInConsultant", json).apply()
        }
    var countryCity: ArrayList<DataCountry>
        get() {
            val list = pref.getString("countryCity", null)
            var Arraylist: ArrayList<DataCountry> = ArrayList()
            if (list != null) {
                try {
                    Arraylist = mapper!!.readValue(list, mapper!!.typeFactory.constructCollectionType(ArrayList::class.java, DataCountry::class.java))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return Arraylist
        }
        set(datacountrylist) {
            try {
                pref_editor.putString("countryCity", mapper!!.writeValueAsString(datacountrylist)).apply()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
}