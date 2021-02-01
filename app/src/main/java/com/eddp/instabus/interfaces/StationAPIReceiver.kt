package com.eddp.instabus.interfaces

import com.eddp.instabus.data.Station

interface StationAPIReceiver {
    fun setAllStations(stations: List<Station>?, err: String? = null) {}
    fun setNearStations(stations: List<Station>?, err: String? = null) {}
}