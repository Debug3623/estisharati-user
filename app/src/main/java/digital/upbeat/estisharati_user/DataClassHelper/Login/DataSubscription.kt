package digital.upbeat.estisharati_user.DataClassHelper.Login

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataSubscription(var courses: String, var consultations: String, var package_count: String) {
    constructor() : this("", "", "")
}