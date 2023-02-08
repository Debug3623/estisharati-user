package estisharati.bussiness.eshtisharati_consultants.DataClassHelper.Login

import org.codehaus.jackson.annotate.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DataSubscription(var courses:String,var consultations:String,val current_package:String){
    constructor():this("","","")
}