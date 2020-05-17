package digital.upbeat.estisharati_user.Helper

import android.content.Context
import android.content.SharedPreferences
import org.codehaus.jackson.map.ObjectMapper

 class SharedPreferencesHelper(  mContext: Context) {
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

    var isUserSignIn: Boolean
        get() = pref.getBoolean("IsUserSignIn", false)
        set(IsUserSignIn) {
            pref_editor.putBoolean("IsUserSignIn", IsUserSignIn).apply()
        }

//    var countryState: ArrayList<Any?>?
//        get() {
//            val list =
//                pref.getString("countrystate", null)
//            var Arraylist: ArrayList<DataCountryList> = ArrayList()
//            if (list != null) {
//                try {
//                    Arraylist = mapper.readValue(
//                        list, mapper!!.typeFactory.constructCollectionType(
//                            ArrayList::class.java, DataCountryList::class.java
//                        )
//                    )
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//            return Arraylist
//        }
//        set(datacountrylist) {
//            try {
//                pref_editor.putString("countrystate", mapper!!.writeValueAsString(datacountrylist))
//                    .apply()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }


}