package digital.upbeat.estisharati_consultant.DataClassHelper

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataUserMetas(
    val job_title: String,
    val city: String,
    val phone_code: String,
    val consultant_cost: String,
    var qualification: String,
    val qualification_brief: String,
    val fire_base_token: String,
    val country: String
) {
    constructor() : this("", "", "", "","", "", "", "")
}