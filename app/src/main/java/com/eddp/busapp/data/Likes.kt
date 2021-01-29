package com.eddp.busapp.data

import com.squareup.moshi.Json

data class LikeResponse(
    @Json(name = "add") var add: Boolean,
    @Json(name = "count") var count: String
)