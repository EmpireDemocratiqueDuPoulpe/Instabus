package com.eddp.busapp.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.R
import com.eddp.busapp.data.Station
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.eddp.busapp.views.recycler_view.GridSpacingItemDecoration
import com.eddp.busapp.views.recycler_view.UserPicAdapter
import com.google.android.material.navigation.NavigationView

class StationNavDrawer : NavigationView, WebServiceReceiver {
    private var _webServiceLink: WebServiceLink? = null

    private var _userPicRecyclerView: RecyclerView? = null
    private val _userPicAdapter = UserPicAdapter { position, item ->
        //onItemClick(position, item)
    }
    private lateinit var _userPicProgressBar: ProgressBar

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    init {
        this._webServiceLink = WebServiceLink(this)
    }

    // Views
    fun setStation(station: Station) {
        this._userPicProgressBar = findViewById(R.id.drawer_user_pic_loading)
        setLoading(true)

        // Change station info
        val title: TextView = findViewById(R.id.drawer_station_name)
        title.text = station.concatName
        title.tag = station.id
        findViewById<TextView>(R.id.drawer_station_address).text = station.streetName
        findViewById<TextView>(R.id.drawer_station_city).text = station.city

        // Add user pics
        fillUserPics(station.id)
    }

    private fun fillUserPics(stationId: Long) {
        this._userPicRecyclerView = findViewById(R.id.drawer_user_pic_recycler_view)
        this._userPicRecyclerView?.layoutManager = GridLayoutManager(context, 2)
        this._userPicRecyclerView?.addItemDecoration(
                GridSpacingItemDecoration(2, 40, true)
        )

        this._userPicRecyclerView?.adapter = this._userPicAdapter

        // Get data
        this._webServiceLink?.getUserPics(1, stationId)
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            this._userPicProgressBar.visibility = View.VISIBLE
            this._userPicRecyclerView?.visibility = View.INVISIBLE
        } else {
            this._userPicProgressBar.visibility = View.INVISIBLE
            this._userPicRecyclerView?.visibility = View.VISIBLE
        }
    }

    // Web service
    override fun setUserPics(userPics: List<UserPic>?) {
        if (userPics != null) {
            this._userPicAdapter.setData(userPics)
        }

        setLoading(false)
    }
}