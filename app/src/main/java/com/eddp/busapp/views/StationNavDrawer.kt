package com.eddp.busapp.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.R
import com.eddp.busapp.data.Station
import com.eddp.busapp.data.UserPic
import com.eddp.busapp.data.WebServiceLink
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.eddp.busapp.views.recycler_view.GridSpacingItemDecoration
import com.eddp.busapp.views.recycler_view.UserPicAdapter
import com.google.android.material.navigation.NavigationView
import java.io.InputStream

class StationNavDrawerLayout : DrawerLayout {
    private var _enableOpenOnSwipe: Boolean = false

    // Getters
    fun isOpenOnSwipeEnabled() = this._enableOpenOnSwipe

    // Setters
    fun setOpenOnSwipeEnabled(enable: Boolean) {
        this._enableOpenOnSwipe = enable
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // Prevent from opening the drawer with a swipe
        if (!this._enableOpenOnSwipe && !isDrawerVisible(GravityCompat.END)) {
            return false
        }

        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        // Prevent from opening the drawer with a swipe
        if (!this._enableOpenOnSwipe && !isDrawerVisible(GravityCompat.END)) {
            return false
        }

        return super.onTouchEvent(ev)
    }
}

class StationNavDrawer : NavigationView, WebServiceReceiver {
    private var _webServiceLink: WebServiceLink? = null

    private var _isInitialized = false
    private lateinit var _stationImg: ImageView
    private lateinit var _stationName: TextView
    private lateinit var _stationStreetName: TextView
    private lateinit var _stationCity: TextView
    private lateinit var _stationPicsCount: TextView

    private lateinit var _userPicProgressBar: ProgressBar

    private lateinit var _userPicRecyclerView: RecyclerView
    private lateinit var _userPicAdapter: UserPicAdapter

    init {
        this._webServiceLink = WebServiceLink(this)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    private fun initView() {
        // Station info
        this._stationImg = findViewById(R.id.drawer_station_img)
        val inputStream: InputStream = resources.openRawResource(R.raw.bus_stop)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        this._stationImg.setImageBitmap(bitmap)

        this._stationName = findViewById(R.id.drawer_station_name)
        this._stationStreetName = findViewById(R.id.drawer_station_address)
        this._stationCity = findViewById(R.id.drawer_station_city)
        this._stationPicsCount = findViewById(R.id.drawer_station_pics_count)

        // User pics
        this._userPicProgressBar = findViewById(R.id.drawer_user_pic_loading)

        this._userPicRecyclerView = findViewById(R.id.drawer_user_pic_recycler_view)
        this._userPicRecyclerView.layoutManager = GridLayoutManager(context, 1)
        this._userPicRecyclerView.addItemDecoration(
                GridSpacingItemDecoration(1, 20, true)
        )

        this._userPicAdapter = UserPicAdapter(context)
        this._userPicRecyclerView.adapter = this._userPicAdapter

        this._isInitialized = true
    }

    // Views
    fun setStation(station: Station) {
        if (!this._isInitialized) initView()
        setLoading(true)

        // Change station info
        this._stationName.text = station.concatName
        this._stationName.tag = station.id
        this._stationStreetName.text = station.streetName
        this._stationCity.text = station.city

        // Add user pics
        fillUserPics(station.id)
    }

    private fun fillUserPics(stationId: Long) {
        this._webServiceLink?.getUserPics(1, stationId)
    }

    fun emptyUserPics() {
        this._userPicAdapter.setData(ArrayList())
    }

    private fun setLoading(loading: Boolean) {
        if (loading) {
            this._userPicProgressBar.visibility = View.VISIBLE
            this._userPicRecyclerView.visibility = View.INVISIBLE
        } else {
            this._userPicProgressBar.visibility = View.INVISIBLE
            this._userPicRecyclerView.visibility = View.VISIBLE
        }
    }

    // Web service
    override fun setUserPics(userPics: List<UserPic>?) {
        if (userPics != null) {
            this._userPicAdapter.setData(userPics)
            this._stationPicsCount.text = userPics.size.toString()
        }

        setLoading(false)
    }
}