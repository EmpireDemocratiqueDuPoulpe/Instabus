package com.eddp.busapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.data.Station
import com.eddp.busapp.interfaces.AsyncDataObserver
import com.eddp.busapp.recycler_view.StationAdapter

class StationsList : Fragment(), AsyncDataObserver {
    private lateinit var _activity: MainActivity

    private var _stationsRecyclerView: RecyclerView? = null
    private val _stationsAdapter = StationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add observer
        this._activity = activity as MainActivity
        this._activity.registerReceiver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_stations_list, container, false)

        // Get and init the recycler view
        this._stationsRecyclerView = v.findViewById(R.id.stations_recycler_view)

        if(this._stationsRecyclerView != null){
            this._stationsRecyclerView!!.layoutManager = LinearLayoutManager(context)
            this._stationsRecyclerView!!.adapter = this._stationsAdapter
        }

        return v
    }

    // Observer
    override fun onDataGet() {
        val stations: List<Station>? = this._activity.getStations()

        if (stations != null) {
            this._stationsAdapter.setData(stations)
        }
    }

}