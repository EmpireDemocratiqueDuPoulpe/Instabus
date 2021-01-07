package com.eddp.busapp.recycler_view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.data.UserPic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserPicAdapter : ListAdapter<UserPic, RecyclerView.ViewHolder>(UserPicDiffCallback()) {
    private val _adapterCoroutine = CoroutineScope(Dispatchers.Default)

    // Setters
    fun setData(list: MutableList<UserPic>) {
        this._adapterCoroutine.launch {
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }

    // Views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserPicViewHolder.from(parent)
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