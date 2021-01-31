package com.eddp.instabus

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.instabus.data.Station
import com.eddp.instabus.interfaces.NeedStations
import com.eddp.instabus.views.recycler_view.StationAdapter

class StationsList : Fragment(), NeedStations {
    private lateinit var _activity: MainActivity

    private var _stationsRecyclerView: RecyclerView? = null
    private var _stationsAdapter: StationAdapter? = null

    // Setters
    override fun setStations(stations: List<Station>) {
        this._stationsAdapter?.setData(stations)
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

        if (this._stationsRecyclerView != null) {
            this._stationsRecyclerView!!.layoutManager = LinearLayoutManager(context)
            this._stationsRecyclerView!!.adapter = this._stationsAdapter

            // Get data if it's already fetched
            this._activity = activity as MainActivity
            val stations: List<Station>? = this._activity.getStations()

            if (stations != null) {
                this._stationsAdapter!!.setData(stations)
            }
        }
    }
}