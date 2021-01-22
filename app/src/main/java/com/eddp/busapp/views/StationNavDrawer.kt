package com.eddp.busapp.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.eddp.busapp.R
import com.eddp.busapp.data.Station
import com.google.android.material.navigation.NavigationView

class StationNavDrawer : NavigationView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    fun setStation(station: Station) {
        val title: TextView = findViewById(R.id.drawer_station_name)
        title.text = station.concatName
        title.tag = station.id
        findViewById<TextView>(R.id.drawer_station_address).text = station.streetName
        findViewById<TextView>(R.id.drawer_station_city).text = station.city
    }
}