package estisharatibussiness.users.com.DataClassHelperMehtods.Search

data class SearchResponse(
    val consultations: List<Consultation>,
    val courses: List<Course>
)