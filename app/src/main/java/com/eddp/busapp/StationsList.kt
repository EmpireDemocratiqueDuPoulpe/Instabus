package com.eddp.busapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.recycler_view.StationAdapter

class StationsList : Fragment() {
    private var _stationsRecyclerView: RecyclerView? = null
    private val _stationsAdapter = StationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_stations_list, container, false)

        this._stationsRecyclerView = v.findViewById(R.id.stations_recycler_view)

        if(this._stationsRecyclerView != null){
            this._stationsRecyclerView!!.layoutManager = LinearLayoutManager(context)

            this._stationsAdapter.setData(
                (activity as MainActivity).getStations() ?: listOf()
            )
            this._stationsRecyclerView!!.adapter = this._stationsAdapter
        }


        return v
    }

}