package com.eddp.busapp.data

import com.squareup.moshi.Json

data class Post(
    var post_id: Int,
    var username: String,
    var title: String,
    var creation_timestamp: String,
    var img_path: String,
    var likes: Int,
)