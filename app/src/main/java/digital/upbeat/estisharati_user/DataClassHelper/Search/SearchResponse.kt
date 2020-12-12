package digital.upbeat.estisharati_user.DataClassHelper.Search

data class SearchResponse(
    val consultations: List<Consultation>,
    val courses: List<Course>
)