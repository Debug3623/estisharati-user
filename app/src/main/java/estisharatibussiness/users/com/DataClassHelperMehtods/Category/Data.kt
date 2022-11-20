package estisharatibussiness.users.com.DataClassHelperMehtods.Category

data class Data(
    val id: String,
    val name: String,
    val parent_id: Any,
    val subcategories: ArrayList<Subcategory>
)