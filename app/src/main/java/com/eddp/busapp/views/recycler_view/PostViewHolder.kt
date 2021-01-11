package com.eddp.busapp.views.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.R
import com.eddp.busapp.data.Post

class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val _title: TextView = view.findViewById(R.id.post_title)
    private val _creationDate: TextView = view.findViewById(R.id.post_creation_date)
    private val _img: ImageView = view.findViewById(R.id.post_img)
    private val _author: TextView = view.findViewById(R.id.post_author)
    private val _likesCount: TextView = view.findViewById(R.id.post_likes_count)

    fun bind(post: Post) {
        this._title.text = post.title
        this._creationDate.text = post.creation_timestamp
        //this._img.setImageURI(post.img_path)
        this._author.text = post.username
        this._likesCount.text = post.likes.toString()
    }

    companion object {
        fun from(parent: ViewGroup) : PostViewHolder {
            val v: View = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.home_recycler_view_item, parent, false)

            return PostViewHolder(v)
        }
    }
}