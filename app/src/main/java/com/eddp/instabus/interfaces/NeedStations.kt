package com.eddp.instabus.interfaces

import com.eddp.instabus.data.Station

interface NeedStations {
    fun setStations(stations: List<Station>)
}