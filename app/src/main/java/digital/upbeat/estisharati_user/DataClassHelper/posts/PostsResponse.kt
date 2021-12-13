package digital.upbeat.estisharati_user.DataClassHelper.posts

data class PostsResponse(
    val `data`: ArrayList<Data>,
    val status: String,
    val message: String
)