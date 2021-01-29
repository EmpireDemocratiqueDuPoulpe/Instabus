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
private const val WEBSERVICE_DEL_POSTS = "delPost.php?"
private const val WEBSERVICE_GET_USERPICS = "getUserPics.php?"
private const val WEBSERVICE_ADD_LIKE = "addLike.php"
private const val WEBSERVICE_ADD_USER = "addUser.php"
private const val WEBSERVICE_LOGIN_USER = "loginUser.php"
private const val WEBSERVICE_GET_PICS_OF = "getPicsOf.php?"
private const val WEBSERVICE_DEFAULT_USER = "BusFucker"

class WebServiceLink constructor(receiver: WebServiceReceiver) {
    private val _receiver: WebServiceReceiver = receiver

    // Posts
    fun getPosts(postId: Long = Long.MIN_VALUE) {
        val call = if (postId != Long.MIN_VALUE)
            service.getPosts(postId) else
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

    fun addPost(userId: Int, stationId: Long, title: String, imgPath: Uri) {
        if (imgPath.path?.isEmpty() == true) return

        // Prepare the query
        val requestUserId = RequestBody.create(MultipartBody.FORM, userId.toString())
        val requestStationId = RequestBody.create(MultipartBody.FORM, stationId.toString())
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

    fun delPost(userId: Long, postId: Long) {
        val call = service.delPost(userId, postId)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val statusCode: Int = response.code()
                val done: Boolean? = response.body()

                if (!response.isSuccessful) {
                    Log.e("WebService", "Error code $statusCode while deleting post")
                    _receiver.deleteSuccessful(false)
                } else {
                    _receiver.deleteSuccessful(done ?: false)
                }
            }

            override fun onFailure(call: Call<Boolean>, err: Throwable) {
                Log.e("WebService", err.message, err)
                _receiver.deleteSuccessful(false)
            }
        })
    }

    // User pics
    fun getUserPics(userId: Long, stationId: Long = Long.MIN_VALUE) {
        val call = if (stationId != Long.MIN_VALUE)
            service.getUserPics(userId, stationId) else
            service.getUserPics(userId)

        call.enqueue(object : Callback<MutableList<UserPic>> {
            override fun onResponse(call: Call<MutableList<UserPic>>, response: Response<MutableList<UserPic>>) {
                val statusCode: Int = response.code()
                val userPics: MutableList<UserPic>? = response.body()

                if (!response.isSuccessful) {
                    Log.e("WebService", "Error code $statusCode while fetching user pics")
                } else {
                    _receiver.setUserPics(userPics)
                }
            }

            override fun onFailure(call: Call<MutableList<UserPic>>, err: Throwable) {
                Log.e("WebService", err.message, err)
            }
        })
    }

    // Likes
    fun addLike(userId: Long, postId: Long) {
        // Prepare the query
        val requestUserId = RequestBody.create(MultipartBody.FORM, userId.toString())
        val requestPostId = RequestBody.create(MultipartBody.FORM, postId.toString())

        // Execute
        val call = service.addLike(requestUserId, requestPostId)

        call.enqueue(object : Callback<LikeResponse> {
            override fun onResponse(call: Call<LikeResponse>, response: Response<LikeResponse>) {
                val statusCode: Int = response.code()
                val resp: LikeResponse? = response.body()

                if (resp != null) {
                    _receiver.addSuccessful(resp.add, resp.count)
                } else {
                    Log.e("WebService", "Error code $statusCode while changing like")
                    _receiver.addSuccessful(false)
                }
            }

            override fun onFailure(call: Call<LikeResponse>, err: Throwable) {
                Log.e("WebService", err.message, err)
                _receiver.addSuccessful(false)
            }
        })
    }

    // Users
    fun addUser(username: String, email: String, password: String) {
        // Prepare the query
        val requestUsername = RequestBody.create(MultipartBody.FORM, username)
        val requestEmail = RequestBody.create(MultipartBody.FORM, email)
        val requestPassword = RequestBody.create(MultipartBody.FORM, password)

        // Execute
        val call = service.addUser(requestUsername, requestEmail, requestPassword)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                val statusCode: Int = response.code()
                val resp: RegisterResponse? = response.body()

                if (resp != null) {
                    _receiver.addSuccessful(resp.status, resp.err ?: "")
                } else {
                    Log.e("WebService", "Error code $statusCode while adding new user")
                    _receiver.addSuccessful(false)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, err: Throwable) {
                Log.e("WebService", err.message, err)
                _receiver.addSuccessful(false)
            }
        })
    }

    fun loginUser(username: String, password: String) {
        // Prepare the query
        val requestUsername = RequestBody.create(MultipartBody.FORM, username)
        val requestPassword = RequestBody.create(MultipartBody.FORM, password)

        // Execute
        val call = service.loginUser(requestUsername, requestPassword)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                val statusCode: Int = response.code()
                val resp: LoginResponse? = response.body()

                if (resp != null) {
                    _receiver.onLogin(resp.status, resp.err ?: "")
                } else {
                    Log.e("WebService", "Error code $statusCode while logging in user")
                    _receiver.onLogin(false)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, err: Throwable) {
                Log.e("WebService", err.message, err)
                _receiver.onLogin(false, err.message ?: "")
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

    @GET(WEBSERVICE_DEL_POSTS)
    fun delPost(@Query("user_id") uid: Long, @Query("post_id") id: Long) : Call<Boolean>

    // User pics
    @GET(WEBSERVICE_GET_PICS_OF)
    fun getUserPics(@Query("user_id") uid: Long) : Call<MutableList<UserPic>>

    @GET(WEBSERVICE_GET_USERPICS)
    fun getUserPics(@Query("user_id") uid: Long, @Query("station_id") id: Long) : Call<MutableList<UserPic>>

    // Likes
    @Multipart
    @POST(WEBSERVICE_ADD_LIKE)
    fun addLike(
        @Part("user_id") uid: RequestBody,
        @Part("post_id") postId: RequestBody,
    ) : Call<LikeResponse>

    // User
    @Multipart
    @POST(WEBSERVICE_ADD_USER)
    fun addUser(
        @Part("username") username: RequestBody,
        @Part("mail") mail: RequestBody,
        @Part("password") password: RequestBody
    ) : Call<RegisterResponse>

    @Multipart
    @POST(WEBSERVICE_LOGIN_USER)
    fun loginUser(
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody
    ) : Call<LoginResponse>
}