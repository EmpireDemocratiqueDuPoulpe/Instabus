package com.eddp.busapp.data

import android.util.Log
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val WEBSERVICE_ADDRESS = "http://90.45.23.115:8080/"
private const val WEBSERVICE_POSTS = "getPost.php?"
private const val WEBSERVICE_USERPICS = "getUserPic.php?"
private const val WEBSERVICE_DEFAULT_USER = "BusFucker"

class WebServiceLink constructor(receiver: WebServiceReceiver) {
    private val _receiver: WebServiceReceiver = receiver

    // Getters
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
                    _receiver.setData(posts)
                }
            }

            override fun onFailure(call: Call<List<Post>>, err: Throwable) {
                Log.e("WebService", err.message, err)
            }
        })
    }

    fun getUserPics(user_id: Long, station_id: Long = Long.MIN_VALUE) {
        val call = if (station_id != Long.MIN_VALUE)
            service.getUserPics(user_id, station_id) else
            service.getUserPics(user_id)

        call.enqueue(object : Callback<List<UserPic>> {
            override fun onResponse(call: Call<List<UserPic>>, response: Response<List<UserPic>>) {
                val statusCode: Int = response.code()
                val userPics: List<UserPic>? = response.body()

                if (!response.isSuccessful) {
                    Log.e("WebService", "Error code $statusCode while fetching posts")
                } else {
                    _receiver.setData(userPics)
                }
            }

            override fun onFailure(call: Call<List<UserPic>>, err: Throwable) {
                Log.e("WebService", err.message, err)
            }
        })
    }

    companion object {
        private var moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        private var retrofit2 = Retrofit.Builder().baseUrl(WEBSERVICE_ADDRESS).addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        private val service = retrofit2.create(WebServiceAPI::class.java)
    }
}

interface WebServiceAPI {
    // Posts
    @GET(WEBSERVICE_POSTS)
    fun getPosts() : Call<List<Post>>

    @GET(WEBSERVICE_POSTS)
    fun getPosts(@Query("post_id") id: Long) : Call<List<Post>>

    // User pics
    @GET(WEBSERVICE_USERPICS)
    fun getUserPics(@Query("user_id") uid: Long) : Call<List<UserPic>>

    @GET(WEBSERVICE_USERPICS)
    fun getUserPics(@Query("user_id") uid: Long, @Query("station_id") id: Long) : Call<List<UserPic>>
}