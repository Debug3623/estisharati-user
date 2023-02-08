package estisharati.bussiness.eshtisharati_consultants.DataClassHelper.CountryCity

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataCountry(val country_id: String, val country_name: String, val cities: ArrayList<DataCity>) {
    constructor() : this("", "", arrayListOf<DataCity>())
}