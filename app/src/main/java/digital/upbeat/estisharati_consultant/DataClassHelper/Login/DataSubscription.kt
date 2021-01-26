package digital.upbeat.estisharati_consultant.DataClassHelper.Login

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataSubscription(var courses:String,var consultations:String,val current_package:String){
    constructor():this("","","")
}