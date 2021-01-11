package com.eddp.busapp.views.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.R
import com.eddp.busapp.data.Station

class StationViewHolder(view: View) : RecyclerView.ViewHolder(view)  {
    private val _name: TextView = view.findViewById(R.id.station_name)
    private val _streetName: TextView = view.findViewById(R.id.station_street_name)
    private val _city: TextView = view.findViewById(R.id.station_city)
    private val _viewStation: ImageButton = view.findViewById(R.id.view_station_btn)

    fun bind(station: Station) {
        this._name.text = "Station n°${station.id}"
        this._streetName.text = station.streetName
        this._city.text = station.city
    }

    companion object {
        fun from(parent: ViewGroup) : StationViewHolder {
            val v: View = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.station_recycler_view_item, parent, false)

            return StationViewHolder(v)
        }
    }
}