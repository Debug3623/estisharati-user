package digital.upbeat.estisharati_user.Helper

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import digital.upbeat.estisharati_user.DataClassHelper.DataCountry
import digital.upbeat.estisharati_user.DataClassHelper.DataUser
import org.codehaus.jackson.map.ObjectMapper
import java.io.IOException

class SharedPreferencesHelper(mContext: Context) {
    private val PREF_NAME = "estisharati_user"
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
    var isUserLogIn: Boolean
        get() = pref.getBoolean("IsUserLogIn", false)
        set(IsUserLogIn) {
            pref_editor.putBoolean("IsUserLogIn", IsUserLogIn).apply()
        }



    var logInUser:DataUser
        get() {
            val gson = Gson()
            val json = pref.getString("DataUser", "")
            if (json.equals("")) {
                return DataUser()
            } else {
                return gson.fromJson(json, DataUser::class.java)
            }
        }
    set(user) {
        val gson = Gson()
        val json = gson.toJson(user)
        pref_editor.putString("DataUser", json).apply()

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