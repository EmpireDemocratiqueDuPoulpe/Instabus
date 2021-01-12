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
private const val WEBSERVICE_DEFAULT_USER = "BusFucker"

class WebServiceLink private constructor(receiver: WebServiceReceiver) {
    private val _receiver: WebServiceReceiver = receiver
    private var _moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private var _retrofit2 = Retrofit.Builder().baseUrl(WEBSERVICE_ADDRESS).addConverterFactory(MoshiConverterFactory.create(this._moshi)).build()
    private val _service = this._retrofit2.create(WebServiceAPI::class.java)

    // Getters
    fun getPosts(post_id: Long = Long.MIN_VALUE) {
        val call = if (post_id != Long.MIN_VALUE)
            this._service.getPosts(post_id) else
            this._service.getPosts()

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

    companion object {
        private var instance: WebServiceLink? = null

        @Synchronized
        fun getInstance(receiver: WebServiceReceiver) : WebServiceLink? {
            if (instance == null) {
                instance = WebServiceLink(receiver)
            }

            return instance
        }


    }
}

interface WebServiceAPI {
    // Posts
    @GET(WEBSERVICE_POSTS)
    fun getPosts() : Call<List<Post>>

    @GET(WEBSERVICE_POSTS)
    fun getPosts(@Query("post_id") id: Long) : Call<List<Post>>
}