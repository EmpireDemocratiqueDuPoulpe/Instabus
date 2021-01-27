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
import com.bumptech.glide.Glide
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.eddp.busapp.views.recycler_view.GridSpacingItemDecoration
import com.eddp.busapp.views.recycler_view.UserPicAdapter

class UserPage : Fragment(), WebServiceReceiver {
    private val _webServiceLink = WebServiceLink(this)
    private var _userPics: List<UserPic> = ArrayList()

    private lateinit var _userProfilePic: ImageView
    private lateinit var _postsCount: TextView
    private lateinit var _likesCount: TextView

    private lateinit var _progressBar: ProgressBar
    private lateinit var _recyclerView: RecyclerView
    private lateinit var _adapter: UserPicAdapter

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
        this._progressBar = view.findViewById(R.id.user_page_pics_loading)
        this._recyclerView = view.findViewById(R.id.user_page_pics_recycler_view)

        this._recyclerView.layoutManager = GridLayoutManager(context, 2)
        this._recyclerView.addItemDecoration(
                GridSpacingItemDecoration(2, 20, true)
        )
        this._adapter = UserPicAdapter(requireContext(), false)
        this._recyclerView.adapter = this._adapter

        this._webServiceLink.getUserPics(1)
    }

    override fun setUserPics(userPics: List<UserPic>?) {
        super.setUserPics(userPics)

        if (userPics != null) {
            this._userPics = userPics
            this._adapter.setData(userPics)
        }

        this._postsCount.text = this._userPics.size.toString()
        this._likesCount.text = this._userPics.sumBy { it.likes }.toString()

        this._progressBar.visibility = View.INVISIBLE
        this._recyclerView.visibility = View.VISIBLE
    }
}