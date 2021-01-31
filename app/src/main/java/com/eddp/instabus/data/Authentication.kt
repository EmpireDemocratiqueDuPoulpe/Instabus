package com.eddp.instabus.data

import com.squareup.moshi.Json

data class RegisterResponse(
    @Json(name = "status") var status: Boolean,
    @Json(name = "selector") var selector: String?,
    @Json(name = "authenticator") var authToken: String?,
    @Json(name = "user_id") var userId: Int?,
    @Json(name = "username") var username: String?,
    @Json(name = "err") var err: String?
)

data class LoginResponse(
     @Json(name = "status") var status: Boolean,
     @Json(name = "selector") var selector: String?,
     @Json(name = "authenticator") var authToken: String?,
     @Json(name = "user_id") var userId: Int?,
     @Json(name = "username") var username: String?,
     @Json(name = "err") var err: String?
)