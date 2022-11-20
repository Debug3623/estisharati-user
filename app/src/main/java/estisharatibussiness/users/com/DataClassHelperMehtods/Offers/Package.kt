package estisharatibussiness.users.com.DataClassHelperMehtods.Offers

import estisharatibussiness.users.com.DataClassHelperMehtods.Packages.Data

data class Package(
    val consultant_id: Any,
    val course_id: Any,
    val discount_rate: String,
    val id: String,
    val offerprice: String,
    val startdate: String,
    val enddate: String,
    val subscription: Data,
    val subscription_id: String,
    val type: String
)