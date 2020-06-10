package digital.upbeat.estisharati_user

class temp {

//    fun LgoinInApiCall(userId: String, password: String) {
//        helperMethods.ShowProgressDialog("Please wait while login in...")
//        val responseBodyCall = retrofitInterface.LOGIN_API_CALL(userId, password)
//        responseBodyCall.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                helperMethods.DismissProgressDialog()
//
//                if (response.isSuccessful) {
//                    if (response.body() != null) {
//                        try {
//                            val jsonObject = JSONObject(response.body()!!.string())
//                            val status = jsonObject.getString("status")
//                            if (status.equals("200")) {
//                                val userString = jsonObject.getString("user")
//                                val userObject = JSONObject(userString)
//                                val id = userObject.getString("id")
//
//                            } else {
//                                val message = jsonObject.getString("message")
//                                helperMethods.AlertPopup("Alert", message)
//                            }
//                        } catch (e: JSONException) {
//                            helperMethods.showToastMessage(getString(R.string.something_went_wrong_on_backend_server))
//                            e.printStackTrace()
//                        } catch (e: IOException) {
//                            e.printStackTrace()
//                        }
//                    } else {
//                        Log.d("body", "Body Empty")
//                    }
//                } else {
//                    helperMethods.showToastMessage(getString(R.string.something_went_wrong))
//                    Log.d("body", "Not Successful")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                helperMethods.DismissProgressDialog()
//                t.printStackTrace()
//                helperMethods.AlertPopup("Alert", getString(R.string.your_network_connection_is_slow_please_try_again))
//            }
//        })
//    }

}