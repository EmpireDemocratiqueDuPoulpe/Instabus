package com.eddp.busapp.interfaces

import com.eddp.busapp.data.Post

interface WebServiceReceiver {
    fun setPosts(posts: List<Post>?)
}