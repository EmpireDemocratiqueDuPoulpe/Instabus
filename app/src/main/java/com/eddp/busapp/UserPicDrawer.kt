package com.eddp.busapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.views.recycler_view.GridSpacingItemDecoration
import com.eddp.busapp.views.recycler_view.UserPicAdapter

class UserPicDrawer : Fragment() {
    private var _userPicRecyclerView: RecyclerView? = null
    private val _userPicAdapter = UserPicAdapter {position, item ->
        onItemClick(position, item)
    }

    // Views
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.station_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillStationInfo(view)
        fillUserPics(view)
    }

    private fun fillStationInfo(view: View) {
        view.findViewById<TextView>(R.id.drawer_station_name).text
    }

    private fun fillUserPics(view: View) {
        this._userPicRecyclerView = view.findViewById(R.id.user_pic_recycler_view)
        this._userPicRecyclerView?.layoutManager = GridLayoutManager(context, 2)
        this._userPicRecyclerView?.addItemDecoration(
                GridSpacingItemDecoration(2, 40, true)
        )

        this._userPicRecyclerView?.adapter = this._userPicAdapter
    }

    // Events
    private fun onItemClick(position: Int, item: UserPic) {
        (activity as MainActivity).closeStationDrawer(item)
    }
}