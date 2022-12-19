package estisharatibussiness.users.com.DataClassHelperMehtods.Home

data class HomeResponse(
    val categories: ArrayList<Category>,
    val consultants: ArrayList<Consultant>,
    val courses: ArrayList<Course>,
    var notification_count: String,
    val slider: ArrayList<Slider>,
    val message_types: ArrayList<MessageTypes>,
    val subscriptions: ArrayList<Subscriptions>
)