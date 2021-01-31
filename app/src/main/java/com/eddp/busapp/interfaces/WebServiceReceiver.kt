package com.eddp.busapp.interfaces

import com.eddp.busapp.data.Post
import com.eddp.busapp.data.UserPic

interface WebServiceReceiver {
    fun setPosts(posts: List<Post>?) {}
    fun setUserPics(userPics: MutableList<UserPic>?) {}
    fun addSuccessful(success: Boolean, message: String = "") {}
    fun deleteSuccessful(success: Boolean) {}
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