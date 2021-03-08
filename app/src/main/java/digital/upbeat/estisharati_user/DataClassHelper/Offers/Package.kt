package digital.upbeat.estisharati_user.DataClassHelper.Offers

import digital.upbeat.estisharati_user.DataClassHelper.Packages.Data

data class Package(
    val consultant_id: Any,
    val course_id: Any,
    val discount_rate: String,
    val enddate: String,
    val id: String,
    val offerprice: String,
    val startdate: String,
    val subscription: Data,
    val subscription_id: String,
    val type: String
)