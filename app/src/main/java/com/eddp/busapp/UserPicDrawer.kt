package com.eddp.busapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.data.Post
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.eddp.busapp.views.recycler_view.GridSpacingItemDecoration
import com.eddp.busapp.views.recycler_view.UserPicAdapter
import org.w3c.dom.Text

class UserPicDrawer : Fragment(), WebServiceReceiver {
    private var _userPicRecyclerView: RecyclerView? = null
    private val _userPicAdapter = UserPicAdapter {position, item ->
        onItemClick(position, item)
    }
    private var _webServiceLink: WebServiceLink? = null

    // Views
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.station_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillUserPics(view)
    }

    private fun fillUserPics(view: View) {
        this._userPicRecyclerView = view.findViewById(R.id.drawer_user_pic_recycler_view)
        this._userPicRecyclerView?.layoutManager = GridLayoutManager(context, 2)
        this._userPicRecyclerView?.addItemDecoration(
                GridSpacingItemDecoration(2, 40, true)
        )

        this._userPicRecyclerView?.adapter = this._userPicAdapter
        // Get data
        this._webServiceLink = WebServiceLink(this)
        Log.d("PROUT", "getUserPics")
        this._webServiceLink?.getUserPics(
            1,
            view.findViewById<TextView>(R.id.drawer_station_name).tag.toString().toLong()
        )
    }

    // Events
    private fun onItemClick(position: Int, item: UserPic) {
        (activity as MainActivity).closeStationDrawer(item)
    }

    override fun setData(data: Any?) {
        Log.d("PROUT", "setData")
        if (data is List<*>) {
            if (data.any { it is UserPic }) {
                this._userPicAdapter.setData(data as MutableList<UserPic>)
            }
        }
    }
}