package com.eddp.busapp.views.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.MainActivity
import com.eddp.busapp.R
import com.eddp.busapp.views.PictureHolder
import com.eddp.busapp.data.Post
import com.eddp.busapp.interfaces.AsyncDataObserver
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

class PostViewHolder(activity: MainActivity, view: View)
    : RecyclerView.ViewHolder(view), AsyncDataObserver {
    private val _activity = activity
    private val _v = view

    private var _post: Post? = null

    init {
        this._activity.registerReceiver(this)
    }

    // View
    fun bind(post: Post) {
        this._post = post

        if (this._post != null) {
            fillView(this._post!!)
        }
    }

    private fun fillView(post: Post) {
        // Get station
        val station = this._activity.getStations()?.find { s -> s.id == post.station_id }

        val postUsername = if (station != null) {
            "${post.username} - ${station.streetName}, ${station.city}"
        } else {
            post.username
        }

        // Set text
        this._v.findViewById<TextView>(R.id.post_title).text = post.title
        this._v.findViewById<TextView>(R.id.post_creation_date).text = post.creation_timestamp
        this._v.findViewById<TextView>(R.id.post_author).text = postUsername
        this._v.findViewById<TextView>(R.id.post_likes_count).text = post.likes.toString()

        // Set station image
        val imageContainer: PictureHolder = this._v.findViewById(R.id.post_img)

        imageContainer
            .setPath(post.img_path)
            .retryOnError(false)
            .addRawFallback(R.raw.missing_picture)
            .load()

        // Add button event listener
        val viewStationBtn: Button = this._v.findViewById(R.id.post_view_station_btn)

        if (station != null) {
            viewStationBtn.isEnabled = true
            viewStationBtn.setOnClickListener {
                this._activity.openStationDrawer(station)
            }
        } else {
            viewStationBtn.isEnabled = false
        }
    }

    // When stations are fetched
    override fun onDataGet() {
        if (this._post != null) {
            fillView(this._post!!)
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