package estisharatibussiness.users.com.DataClassHelper.CityCountry

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataCity(val city_id: String, val city_name: String) {
    constructor() : this("", "")
}