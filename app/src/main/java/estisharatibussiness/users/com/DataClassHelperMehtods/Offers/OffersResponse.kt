package estisharatibussiness.users.com.DataClassHelperMehtods.Offers


data class OffersResponse(
    val consultants: ArrayList<Consultant>,
    val courses: ArrayList<Course>,
    val packages: ArrayList<Package>,
)