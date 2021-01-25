package com.eddp.busapp.data

import android.net.Uri
import android.util.Log
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.File

private const val WEBSERVICE_ADDRESS = "http://90.45.23.115:8080/"
private const val WEBSERVICE_GET_POSTS = "getPost.php?"
private const val WEBSERVICE_ADD_POSTS = "addPost.php"
private const val WEBSERVICE_GET_USERPICS = "getUserPics.php?"
private const val WEBSERVICE_DEFAULT_USER = "BusFucker"

class WebServiceLink constructor(receiver: WebServiceReceiver) {
    private val _receiver: WebServiceReceiver = receiver

    // Posts
    fun getPosts(post_id: Long = Long.MIN_VALUE) {
        val call = if (post_id != Long.MIN_VALUE)
            service.getPosts(post_id) else
            service.getPosts()

        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                val statusCode: Int = response.code()
                val posts: List<Post>? = response.body()

                if (!response.isSuccessful) {
                    Log.e("WebService", "Error code $statusCode while fetching posts")
                } else {
                    _receiver.setPosts(posts)
                }
            }

            override fun onFailure(call: Call<List<Post>>, err: Throwable) {
                Log.e("WebService", err.message, err)
            }
        })
    }

    fun addPost(user_id: Int, station_id: Long, title: String, imgPath: Uri) {
        if (imgPath.path?.isEmpty() == true) return

        // Prepare the query
        val requestUserId = RequestBody.create(MultipartBody.FORM, user_id.toString())
        val requestStationId = RequestBody.create(MultipartBody.FORM, station_id.toString())
        val requestTitle = RequestBody.create(MultipartBody.FORM, title)

        val file = File(imgPath.path!!)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val bodyFile = MultipartBody.Part.createFormData("image", file.name, requestFile)

        // Execute
        val call = service.addPost(requestUserId, requestStationId, requestTitle, bodyFile)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val statusCode: Int = response.code()

                if (!response.isSuccessful) {
                    Log.e("WebService", "Error code $statusCode while adding posts")
                    _receiver.addSuccessful(false)
                } else {
                    _receiver.addSuccessful(true)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, err: Throwable) {
                Log.e("WebService", err.message, err)
                _receiver.addSuccessful(false)
            }
        })
    }

    // User pics
    fun getUserPics(user_id: Long, station_id: Long = Long.MIN_VALUE) {
        val call = if (station_id != Long.MIN_VALUE)
            service.getUserPics(user_id, station_id) else
            service.getUserPics(user_id)

        call.enqueue(object : Callback<List<UserPic>> {
            override fun onResponse(call: Call<List<UserPic>>, response: Response<List<UserPic>>) {
                val statusCode: Int = response.code()
                val userPics: List<UserPic>? = response.body()

                if (!response.isSuccessful) {
                    Log.e("WebService", "Error code $statusCode while fetching user pics")
                } else {
                    _receiver.setUserPics(userPics)
                }
            }

            override fun onFailure(call: Call<List<UserPic>>, err: Throwable) {
                Log.e("WebService", err.message, err)
            }
        })
    }

    companion object {
        private var moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        private var retrofit2 = Retrofit.Builder().baseUrl(WEBSERVICE_ADDRESS)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        private val service = retrofit2.create(WebServiceAPI::class.java)
    }
}

interface WebServiceAPI {
    // Posts
    @GET(WEBSERVICE_GET_POSTS)
    fun getPosts() : Call<List<Post>>

    @GET(WEBSERVICE_GET_POSTS)
    fun getPosts(@Query("post_id") id: Long) : Call<List<Post>>

    @Multipart
    @POST(WEBSERVICE_ADD_POSTS)
    fun addPost(
        @Part("user_id") user_id: RequestBody,
        @Part("station_id") station_id: RequestBody,
        @Part("title") title: RequestBody,
        @Part file: MultipartBody.Part
    ) : Call<ResponseBody>

    // User pics
    @GET(WEBSERVICE_GET_USERPICS)
    fun getUserPics(@Query("user_id") uid: Long) : Call<List<UserPic>>

    @GET(WEBSERVICE_GET_USERPICS)
    fun getUserPics(@Query("user_id") uid: Long, @Query("station_id") id: Long) : Call<List<UserPic>>
}