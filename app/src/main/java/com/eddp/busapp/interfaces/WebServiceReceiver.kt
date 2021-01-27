package com.eddp.busapp.interfaces

import com.eddp.busapp.data.Post
import com.eddp.busapp.data.UserPic

interface WebServiceReceiver {
    fun setPosts(posts: List<Post>?) {}
    fun setUserPics(userPics: List<UserPic>?) {}
    fun addSuccessful(success: Boolean, message: String = "") {}
    fun deleteSuccessful(success: Boolean) {}
}