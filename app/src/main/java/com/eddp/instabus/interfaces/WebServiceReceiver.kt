package com.eddp.instabus.interfaces

import com.eddp.instabus.data.Post
import com.eddp.instabus.data.UserPic

interface WebServiceReceiver {
    fun setPosts(posts: List<Post>?) {}
    fun setUserPics(userPics: MutableList<UserPic>?) {}
    fun addSuccessful(success: Boolean, message: String = "") {}
    fun deleteSuccessful(success: Boolean) {}
    fun hasLiked(liked: Boolean) {}
    fun onRegister(
            registered: Boolean,
            selector: String = "",
            authToken: String = "",
            userId: Int = Int.MIN_VALUE,
            username: String = "",
            err: String = ""
    ) {}
    fun onLogin(
        loggedIn: Boolean,
        selector: String = "",
        authToken: String = "",
        userId: Int = Int.MIN_VALUE,
        username: String = "",
        err: String = ""
    ) {}
}