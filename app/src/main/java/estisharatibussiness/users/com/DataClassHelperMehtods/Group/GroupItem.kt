package estisharatibussiness.users.com.DataClassHelperMehtods.Group

import org.codehaus.jackson.annotate.JsonIgnoreProperties
import java.util.*
import kotlin.collections.ArrayList

@JsonIgnoreProperties(ignoreUnknown = true) data class GroupItem(var group_id: String, val creater_id: String, val group_name: String, val group_image: String, val group_members: ArrayList<String>, val active: Boolean, val time: Date) {
    constructor() : this("", "", "", "", arrayListOf(), false, Date())
}
