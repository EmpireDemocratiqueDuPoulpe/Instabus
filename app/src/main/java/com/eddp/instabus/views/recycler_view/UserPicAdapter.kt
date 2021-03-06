package com.eddp.instabus.views.recycler_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eddp.instabus.R
import com.eddp.instabus.data.UserPic
import com.eddp.instabus.data.WebServiceLink
import com.eddp.instabus.interfaces.WebServiceReceiver
import com.eddp.instabus.views.PictureHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserPicAdapter(context: Context, hideDelete: Boolean = true, deleteCallback : ((item: UserPic) -> Unit)? = null)
    : ListAdapter<UserPic, RecyclerView.ViewHolder>(UserPicDiffCallback()) {
    private val _context = context
    private val _hideDelete = hideDelete
    private val _deleteCallback = deleteCallback
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
        return UserPicViewHolder.from(parent, this._context, this._hideDelete, this._deleteCallback)
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

class UserPicViewHolder(
    view: View,
    context: Context,
    hideDelete: Boolean,
    deleteCallback : ((item: UserPic) -> Unit)?
) : RecyclerView.ViewHolder(view), WebServiceReceiver {
    private var _userId: Long = PreferenceManager.getDefaultSharedPreferences(context).getInt("user_id", Int.MIN_VALUE).toLong()
    private var _webServiceLink = WebServiceLink(this)

    private var _context = context
    private val _hideDelete = hideDelete
    private val _deleteCallback = deleteCallback
    private val _v = view

    private var _userPic: UserPic? = null

    fun bind(userPic: UserPic) {
        this._userPic = userPic

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
        val delBtn : ImageButton = this._v.findViewById(R.id.user_pic_delete_btn)

        if (this._hideDelete) {
            delBtn.visibility = View.GONE
        } else {
            delBtn.visibility = View.VISIBLE
            delBtn.setOnClickListener {
                AlertDialog
                    .Builder(this._context)
                    .setTitle(this._context.getString(R.string.post_delete_confirm_title))
                    .setMessage(this._context.getString(R.string.post_delete_confirm))
                    .setNegativeButton(this._context.getString(R.string.post_delete_confirm__no_btn)) { _, _ -> }
                    .setPositiveButton(this._context.getString(R.string.post_delete_confirm__yes_btn)) { _, _ ->
                        this._webServiceLink.delPost(this._userId, userPic.post_id.toLong())
                    }
                    .show()
            }
        }

    }

    override fun deleteSuccessful(success: Boolean) {
        super.deleteSuccessful(success)

        if (success) {
            if (this._deleteCallback != null && this._userPic != null) {
                this._deleteCallback.invoke(this._userPic!!)
            }
        } else {
            AlertDialog
                .Builder(this._context)
                .setTitle(this._context.getString(R.string.post_delete_error_title))
                .setMessage(this._context.getString(R.string.post_delete_error))
                .setPositiveButton(this._context.getString(R.string.post_delete_error_btn)) { _, _ -> }
                .show()
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            context: Context,
            hideDelete: Boolean,
            deleteCallback : ((item: UserPic) -> Unit)?
        ) : UserPicViewHolder {
            val v: View = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.user_pic_recycler_view_item, parent, false)

            return UserPicViewHolder(v, context, hideDelete, deleteCallback)
        }
    }
}