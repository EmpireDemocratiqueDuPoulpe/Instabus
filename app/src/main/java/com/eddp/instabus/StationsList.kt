package com.eddp.instabus

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eddp.instabus.data.Station
import com.eddp.instabus.interfaces.NeedStations
import com.eddp.instabus.views.recycler_view.StationAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Duration

class StationsList : Fragment(), NeedStations {
    private lateinit var _activity: MainActivity

    private var _swipeToRefresh: SwipeRefreshLayout? = null
    private lateinit var _stationsRecyclerView: RecyclerView
    private var _stationsAdapter: StationAdapter? = null
    private lateinit var _backToTopBtn: FloatingActionButton

    // Setters
    override fun setStations(stations: List<Station>) {
        this._stationsAdapter?.setData(stations)
        this._swipeToRefresh?.isRefreshing = false
    }

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activity: Activity? = activity

        if (activity is MainActivity) {
            this._stationsAdapter = StationAdapter(activity)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stations_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillStationsRecyclerView(view)
    }

    private fun fillStationsRecyclerView(view: View) {
        this._stationsRecyclerView = view.findViewById(R.id.stations_recycler_view)

        this._stationsRecyclerView.layoutManager = LinearLayoutManager(context)
        this._stationsRecyclerView.adapter = this._stationsAdapter

        // Get data if it's already fetched
        this._activity = activity as MainActivity
        val stations: List<Station>? = this._activity.getStations()

        if (stations != null) {
            this._stationsAdapter!!.setData(stations)
        }

        // Swipe to refresh
        this._swipeToRefresh = view.findViewById(R.id.station_refresh_layout)
        this._swipeToRefresh?.setOnRefreshListener {
            this._activity.reloadStations()
        }

        // Back to top
        this._backToTopBtn = view.findViewById(R.id.station_back_to_top_btn)
        this._backToTopBtn.setOnClickListener {
            this._stationsRecyclerView.smoothScrollToPosition(0)
            this._backToTopBtn.visibility = View.GONE
        }
        this._stationsRecyclerView.addOnScrollListener(ShowBackToTopOnScroll(this._backToTopBtn))
    }
}

class ShowBackToTopOnScroll(backToTopBtn: FloatingActionButton) : RecyclerView.OnScrollListener() {
    private val _backToTopBtn = backToTopBtn

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        // Scrolling down
        if (dy > 0) {
            hideButton(true, 0)
        // Scrolling top
        } else if (dy < 0) {
            hideButton(false)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

        // No scrolling
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            hideButton(true)
        }
    }

    private fun hideButton(hide: Boolean, delay: Long = 2000) {
        if (hide) {
            if (this._backToTopBtn.visibility != View.GONE) {
                Handler(Looper.getMainLooper()).postDelayed({
                    this._backToTopBtn.visibility = View.GONE
                }, delay)
            }
        } else {
            this._backToTopBtn.visibility = View.VISIBLE
        }
    }
}