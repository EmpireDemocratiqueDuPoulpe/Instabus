package com.eddp.busapp.data

import com.squareup.moshi.Json

data class RegisterResponse(
    @Json(name = "status") var status: Boolean,
    @Json(name = "err") var err: String?
)

data class LoginResponse(
        @Json(name = "status") var status: Boolean,
        @Json(name = "err") var err: String?
)