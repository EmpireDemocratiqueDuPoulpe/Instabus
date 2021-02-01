package com.eddp.instabus.data

import android.util.Log
import android.widget.Toast
import com.eddp.instabus.R
import com.eddp.instabus.interfaces.StationAPIReceiver
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val STATION_API_ADDRESS = "http://barcelonaapi.marcpous.com"
private const val STATION_API_GET_ALL = "/bus/stations.json"
private const val STATION_API_GET_NEAR = "/bus/nearstation/latlon/{lat}/{lon}/1.json"

class StationAPILink constructor(receiver: StationAPIReceiver) {
    private val _receiver: StationAPIReceiver = receiver

    fun getAllStations() {
        val call : Call<AllStationResponse> = service.getAllStations()

        call.enqueue(object : Callback<AllStationResponse> {
            override fun onResponse( call: Call<AllStationResponse>, response: Response<AllStationResponse>) {
                val statusCode: Int = response.code()
                val resp: AllStationResponse? = response.body()

                if (!response.isSuccessful) {
                    _receiver.setAllStations(null, "Error code $statusCode while fetching all stations")
                } else {
                    _receiver.setAllStations(resp?.data?.stations)
                }
            }

            override fun onFailure(call: Call<AllStationResponse>?, err: Throwable) {
                Log.e("StationAPI", err.message, err)
                _receiver.setAllStations(null, err.message)
            }
        })
    }

    fun getStationsNear(lat: String? = null, lon: String? = null) {
        val call = when {
            lat != null && lon == null -> service.getStationsNear(lat)
            lat == null && lon != null -> service.getStationsNear("41.404377", lon)
            lat != null && lon != null -> service.getStationsNear(lat, lon)
            else -> null
        }

        call?.enqueue(object : Callback<NearStationResponse> {
            override fun onResponse( call: Call<NearStationResponse>, response: Response<NearStationResponse>) {
                val statusCode: Int = response.code()
                val resp: NearStationResponse? = response.body()

                if (!response.isSuccessful) {
                    _receiver.setNearStations(null, "Error code $statusCode while fetching near stations")
                } else {
                    _receiver.setNearStations(resp?.data?.stations)
                }
            }

            override fun onFailure(call: Call<NearStationResponse>?, err: Throwable) {
                Log.e("StationAPI", err.message, err)
                _receiver.setNearStations(null, err.message)
            }
        })
    }

    companion object {
        private var moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        private var retrofit2 = Retrofit.Builder().baseUrl(STATION_API_ADDRESS)
                .addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        private val service = retrofit2.create(StationAPI::class.java)
    }
}

interface StationAPI {
    @GET(STATION_API_GET_ALL)
    fun getAllStations() : Call<AllStationResponse>

    @GET(STATION_API_GET_NEAR)
    fun getStationsNear(
        @Path("lat") lat: String = "41.404377",
        @Path("lon") lon: String = "2.175471"
    ) : Call<NearStationResponse>
}