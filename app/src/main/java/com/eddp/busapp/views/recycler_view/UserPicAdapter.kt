package com.eddp.busapp.views.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.R
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.views.PictureHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserPicAdapter(private val onItemClick: ((position: Int, item: UserPic) -> Unit))
    : ListAdapter<UserPic, RecyclerView.ViewHolder>(UserPicDiffCallback()) {
    private val _adapterCoroutine = CoroutineScope(Dispatchers.Default)

    // Setters
    fun setData(list: List<UserPic>) {
        this._adapterCoroutine.launch {
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }

    // Views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserPicViewHolder.from(parent, onItemClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserPicViewHolder -> {
                val userPic = getItem(position) as UserPic
                holder.bind(userPic)
            }
        }
    }
}

class UserPicDiffCallback : DiffUtil.ItemCallback<UserPic>() {
    override fun areItemsTheSame(oldItem: UserPic, newItem: UserPic): Boolean {
        return oldItem.post_id == newItem.post_id
    }

    override fun areContentsTheSame(oldItem: UserPic, newItem: UserPic): Boolean {
        return oldItem == newItem
    }
}

class UserPicViewHolder(view: View, private val onItemClick: ((position: Int, item: UserPic) -> Unit))
    : RecyclerView.ViewHolder(view) {
    private val _v = view

    fun bind(userPic: UserPic) {
        // Set text
        this._v.findViewById<TextView>(R.id.user_pic_title).text = userPic.title
        this._v.findViewById<TextView>(R.id.user_pic_likes_count).text = userPic.likes.toString()

        // Set station image
        val imageContainer: PictureHolder = this._v.findViewById(R.id.user_pic_img)

        imageContainer
            .setPath(userPic.img_path)
            .retryOnError(false)
            .addRawFallback(R.raw.missing_picture)
            .load()

        // Add button event listener
        this._v.setOnClickListener {
            userPic.let {
                onItemClick.invoke(adapterPosition, userPic)
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup, onItemClick: ((position: Int, item: UserPic) -> Unit)): UserPicViewHolder {
            val v: View = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.user_pic_recycler_view_item, parent, false)

            return UserPicViewHolder(v, onItemClick)
        }
    }
}