package com.eddp.instabus.views.recycler_view

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eddp.instabus.MainActivity
import com.eddp.instabus.R
import com.eddp.instabus.data.Post
import com.eddp.instabus.data.WebServiceLink
import com.eddp.instabus.interfaces.AsyncDataObserver
import com.eddp.instabus.interfaces.WebServiceReceiver
import com.eddp.instabus.views.PictureHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(activity: MainActivity) : ListAdapter<Post, RecyclerView.ViewHolder>(
    PostDiffCallback()
) {
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
    : RecyclerView.ViewHolder(view), AsyncDataObserver, WebServiceReceiver {
    private var _userId: Long = PreferenceManager.getDefaultSharedPreferences(activity).getInt("user_id", Int.MIN_VALUE).toLong()
    private var _webServiceLink = WebServiceLink(this)
    private val _activity = activity

    private val _v = view
    private lateinit var _likes: TextView

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
        formatDate(post.creation_timestamp)
        this._v.findViewById<TextView>(R.id.post_author).text = postUsername
        this._likes = this._v.findViewById(R.id.post_likes_count)
        this._likes.text = post.likes.toString()

        // Has liked?
        this._webServiceLink.hasUserLiked(this._userId, post.post_id.toLong())

        // Set station image
        val imageContainer: PictureHolder = this._v.findViewById(R.id.post_img)

        imageContainer
            .setPath(post.img_path)
            .retryOnError(false)
            .addRawFallback(R.raw.missing_picture)
            .load()

        // Add likes
        this._likes.setOnClickListener {
            this._webServiceLink.addLike(this._userId, post.post_id.toLong())
        }

        // Add view station button event listener
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

    private fun formatDate(dateTime: String) {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRENCH)
        val dateView: TextView = this._v.findViewById(R.id.post_creation_date)

        try {
            // Parse the date and make the diff
            val creationDate = format.parse(dateTime) ?: throw Exception()
            val currentDate = Date()
            val diff: Long = currentDate.time - creationDate.time

            // Get the time
            val seconds: Long = diff / 1000
            val minutes: Long = seconds / 60
            val hours: Long = minutes / 60
            val days: Long = hours / 24
            val weeks: Long = days / 7
            val months: Long = weeks / 4
            val years: Long = months / 12

            // Build the string
            val textFormat = this._activity.getString(R.string.date_format)
            val durationText: String = when {
                years >= 1 -> {
                    val dur = if (years > 1) R.string.date_years; else R.string.date_year
                    String.format(this._activity.getString(dur), years)
                }
                months >= 1 -> {
                    val dur = if (months > 1) R.string.date_months; else R.string.date_month
                    String.format(this._activity.getString(dur), months)
                }
                weeks >= 1 -> {
                    val dur = if (weeks > 1) R.string.date_weeks; else R.string.date_week
                    String.format(this._activity.getString(dur), weeks)
                }
                days >= 1 -> {
                    val dur = if (days > 1) R.string.date_days; else R.string.date_day
                    String.format(this._activity.getString(dur), days)
                }
                hours >= 1 -> {
                    val dur = if (hours > 1) R.string.date_hours; else R.string.date_hour
                    String.format(this._activity.getString(dur), hours)
                }
                minutes >= 1 -> {
                    val dur = if (minutes > 1) R.string.date_minutes; else R.string.date_minute
                    String.format(this._activity.getString(dur), minutes)
                }
                else -> {
                    val dur = if (seconds > 1) R.string.date_seconds; else R.string.date_second
                    String.format(this._activity.getString(dur), seconds)
                }
            }

            // Set the text
            dateView.text = String.format(textFormat, durationText)
        } catch (err: Exception) {
            dateView.text = dateTime
        }
    }

    // When stations are fetched
    override fun onDataGet() {
        if (this._post != null) {
            fillView(this._post!!)
        }
    }

    override fun addSuccessful(success: Boolean, message: String) {
        super.addSuccessful(success, message)

        hasLiked(success)
        if (message.isNotEmpty()) this._likes.text = message
    }

    override fun hasLiked(liked: Boolean) {
        super.hasLiked(liked)

        // Set drawable
        val size: Rect = this._likes.compoundDrawables.first().bounds
        val drawable: Drawable?

        if (liked) {
            drawable = ContextCompat.getDrawable(this._activity, R.drawable.ic_favorite_24px)
                ?.let { DrawableCompat.wrap(it) }

            if (drawable != null) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this._activity, R.color.red_500))
            }
        } else {
            drawable = ContextCompat.getDrawable(this._activity, R.drawable.ic_favorite_border_24px)
                ?.let { DrawableCompat.wrap(it) }

            if (drawable != null) {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this._activity, R.color.icon_tint))
            }
        }

        drawable?.bounds = size

        this._likes.setCompoundDrawables(drawable, null, null, null)
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