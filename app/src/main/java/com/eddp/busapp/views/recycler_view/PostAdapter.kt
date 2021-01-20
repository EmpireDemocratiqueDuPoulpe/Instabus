package com.eddp.busapp.views.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.MainActivity
import com.eddp.busapp.R
import com.eddp.busapp.data.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(activity: MainActivity) : ListAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {
    private val _activity = activity

    private val _adapterCoroutine = CoroutineScope(Dispatchers.Default)

    // Setters
    fun setData(list: MutableList<Post>) {
        this._adapterCoroutine.launch {
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }

    // Views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PostViewHolder.from(this._activity, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> {
                val post = getItem(position) as Post
                holder.bind(post)
            }
        }
    }
}

// Diff Callback
class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.post_id == newItem.post_id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostViewHolder(activity: MainActivity, view: View) : RecyclerView.ViewHolder(view) {
    private val _activity = activity
    private val _v = view

    fun bind(post: Post) {
        this._v.findViewById<TextView>(R.id.post_title).text = post.title
        this._v.findViewById<TextView>(R.id.post_creation_date).text = post.creation_timestamp
        // Todo: Set station img
        this._v.findViewById<TextView>(R.id.post_author).text = post.username
        this._v.findViewById<TextView>(R.id.post_likes_count).text = post.likes.toString()

        this._v.findViewById<Button>(R.id.post_view_station_btn).setOnClickListener {
            //this._activity.openStationDrawer(post.station_id)
        }
    }

    companion object {
        fun from(activity: MainActivity, parent: ViewGroup) : PostViewHolder {
            val v: View = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.home_recycler_view_item, parent, false)

            return PostViewHolder(activity, v)
        }
    }
}