package digital.upbeat.estisharati_user.DataClassHelper.Favourites

data class FavouritesResponse(
    val consultants: ArrayList<Consultant>,
    val courses: ArrayList<Course>
)