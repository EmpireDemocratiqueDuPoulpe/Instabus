package com.eddp.busapp.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.R
import com.eddp.busapp.data.UserPic

class UserPicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val _title: TextView = view.findViewById(R.id.user_pic_title)
    private val _creationDate: TextView = view.findViewById(R.id.user_pic_creation_date)
    private val _img: ImageView = view.findViewById(R.id.user_pic_img)
    private val _likesCount: TextView = view.findViewById(R.id.user_pic_likes_count)

    fun bind(userPic: UserPic) {
        this._title.text = userPic.title
        this._creationDate.text = userPic.creation_timestamp
        //this._img.setImageURI(userPic.img_path)
        this._likesCount.text = userPic.likes.toString()
    }

    companion object {
        fun from(parent: ViewGroup) : UserPicViewHolder {
            val v: View = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.user_pic_recycler_view_item, parent, false)

            return UserPicViewHolder(v)
        }
    }
}