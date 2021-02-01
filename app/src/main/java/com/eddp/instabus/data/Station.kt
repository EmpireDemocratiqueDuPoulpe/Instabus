package com.eddp.instabus.data

import com.eddp.instabus.MainActivity
import com.eddp.instabus.R
import com.squareup.moshi.Json
import kotlin.math.round

data class AllStationResponse (
        @Json(name = "code") var code: Int,
        @Json(name = "data") var data: AllStation
)

data class NearStationResponse (
    @Json(name = "code") var code: Int,
    @Json(name = "data") var data: NearStation
)

data class AllStation(
    @Json(name = "tmbs") var stations: List<Station>
)

data class NearStation(
    @Json(name = "nearstations") var stations: List<Station>
)

data class Station(
    @Json(name = "id") var id: Long,
    @Json(name = "street_name") var streetName: String?,
    @Json(name = "city") var city: String?,
    @Json(name = "utm_x") var utmX: String?,
    @Json(name = "utm_y") var utmY: String?,
    @Json(name = "lat") var latitude: String?,
    @Json(name = "lon") var longitude: String?,
    @Json(name = "furniture") var furniture: String?,
    @Json(name = "buses") var buses: String?,
    @Json(name = "distance") var distance: Float?,
) {
    var concatName: String = String.format(MainActivity.getResources()?.getString(R.string.station_concat_name) ?: "", id)
    var concatBuses: String = String.format(MainActivity.getResources()?.getString(R.string.station_concat_buses) ?: "", buses)
    var concatDistance: String = String.format(MainActivity.getResources()?.getString(R.string.station_concat_distance) ?: "", round(distance?.times(1000) ?: 0f))
}