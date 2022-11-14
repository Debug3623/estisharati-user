package estisharatibussiness.users.com.DataClassHelper.posts

data class PostsResponse(
    val `data`: ArrayList<Data>,
    val status: String,
    val message: String
)