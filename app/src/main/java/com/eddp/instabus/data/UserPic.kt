package com.eddp.instabus.data

data class UserPic(
    var post_id: Int,
    var user_id: Int?,
    var title: String,
    var creation_timestamp: String,
    var img_path: String,
    var likes: Int,
)