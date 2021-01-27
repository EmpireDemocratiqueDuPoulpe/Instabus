package com.eddp.busapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.eddp.busapp.views.recycler_view.GridSpacingItemDecoration
import com.eddp.busapp.views.recycler_view.UserPicAdapter

class UserPage : Fragment(), WebServiceReceiver {
    private val _webServiceLink = WebServiceLink(this)
    private var _userPics: MutableList<UserPic> = ArrayList()

    private lateinit var _userProfilePic: ImageView
    private lateinit var _postsCount: TextView
    private lateinit var _likesCount: TextView

    private lateinit var _swipeToRefresh: SwipeRefreshLayout
    private lateinit var _progressBar: ProgressBar
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _adapter: UserPicAdapter

    // Setters
    private fun setLoading(loading: Boolean) {
        if (loading) {
            this._recyclerView.visibility = View.INVISIBLE
            this._progressBar.visibility = View.VISIBLE
        } else {
            this._recyclerView.visibility = View.VISIBLE
            this._progressBar.visibility = View.INVISIBLE
            this._swipeToRefresh.isRefreshing = false
        }
    }

    // Views
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillUserInfo(view)
        fillUserPics(view)
    }

    private fun fillUserInfo(view: View) {
        this._userProfilePic = view.findViewById(R.id.user_page_picture)
        Glide.with(this).load(R.raw.user_profile_pic).into(this._userProfilePic)

        this._postsCount = view.findViewById(R.id.user_page_posts_count_number)
        this._likesCount = view.findViewById(R.id.user_page_likes_count_number)
    }

    private fun fillUserPics(view: View) {
        // Recycler view
        this._progressBar = view.findViewById(R.id.user_page_pics_loading)
        this._recyclerView = view.findViewById(R.id.user_page_pics_recycler_view)

        this._recyclerView.layoutManager = GridLayoutManager(context, 2)
        this._recyclerView.addItemDecoration(
                GridSpacingItemDecoration(2, 20, true)
        )
        this._adapter = UserPicAdapter(requireContext(), false) { item -> onUserPicDeletion(item) }
        this._recyclerView.adapter = this._adapter

        this.setLoading(true)
        this._webServiceLink.getUserPics(1)

        // Swipe to refresh
        this._swipeToRefresh = view.findViewById(R.id.user_page_pics_swipe_refresh)
        this._swipeToRefresh.setOnRefreshListener {
            this._webServiceLink.getUserPics(1)
        }
    }

    private fun onUserPicDeletion(item: UserPic) {
        this.setLoading(true)
        this._webServiceLink.getUserPics(1)
    }

    override fun setUserPics(userPics: MutableList<UserPic>?) {
        super.setUserPics(userPics)

        if (userPics != null) {
            this._userPics = userPics
            this._adapter.setData(userPics)
        }

        this._postsCount.text = this._userPics.size.toString()
        this._likesCount.text = this._userPics.sumBy { it.likes }.toString()

        setLoading(false)
    }
}