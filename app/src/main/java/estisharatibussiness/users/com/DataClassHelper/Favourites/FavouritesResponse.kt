package estisharatibussiness.users.com.DataClassHelper.Favourites

data class FavouritesResponse(
    val consultants: ArrayList<Consultant>,
    val courses: ArrayList<Course>
)