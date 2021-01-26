package digital.upbeat.estisharati_user.DataClassHelper.Category

data class Data(
    val id: String,
    val name: String,
    val parent_id: Any,
    val subcategories: ArrayList<Subcategory>
)