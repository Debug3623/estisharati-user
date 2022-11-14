package estisharatibussiness.users.com.DataClassHelper.Search

data class SearchResponse(
    val consultations: List<Consultation>,
    val courses: List<Course>
)