package digital.upbeat.estisharati_user.DataClassHelper

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataSubscription(val courses:String,val consultations:String,val current_package:String){
    constructor():this("","","")
}