package com.eddp.busapp.interfaces

import com.eddp.busapp.data.Station

interface NeedStations {
    fun getStations(stations: List<Station>)
}