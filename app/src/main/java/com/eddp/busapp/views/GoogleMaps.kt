package com.eddp.busapp.views

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import com.eddp.busapp.data.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*

class MapReady(style: String?, marker: Drawable?) : OnMapReadyCallback {
    private var _map: GoogleMap? = null
    private var _isMapReady: Boolean = false
    private var _style: String? = style
    private var _maker: BitmapDescriptor = getMarkerIconFromDrawable(marker)

    private var _stations: List<Station> = listOf()
    private var _markers: MutableList<Marker?> = ArrayList()

    constructor(style: String) : this(style, null)
    constructor(marker: Drawable?) : this(null, marker)

    // Getters
    private fun getMarkerIconFromDrawable(drawable: Drawable?) : BitmapDescriptor {
        if (drawable == null) return BitmapDescriptorFactory.defaultMarker()

        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
        )

        canvas.setBitmap(bitmap)

        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun isMapReady() : Boolean = this._isMapReady

    // Setters
    fun setStations(stations: List<Station>) {
        this._stations = stations

        if (this._isMapReady) {
            redrawStationMarkers()
        }
    }

    // Google Maps
    override fun onMapReady(map: GoogleMap?) {
        this._map = map

        // Move the map onto Barcelona
        val barcelona = LatLng(41.404377, 2.175471)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 16f))

        // Update map style
        if (this._style != null) {
            try {
                val newStyle: Boolean = map?.setMapStyle(MapStyleOptions(this._style)) ?: false

                if (!newStyle) Log.e("Map", "Failed to parse style file.")
            } catch (err: Resources.NotFoundException) {
                Log.e("Map", err.message, err)
            }
        }

        // Add markers
        createStationsMarkers()

        this._isMapReady = true
    }

    private fun createStationsMarkers() {
        for (station in _stations) {
            if(station.latitude == null || station.longitude == null) continue

            val marker = this._map?.addMarker(MarkerOptions()
                .position(LatLng(station.latitude!!.toDouble(), station.longitude!!.toDouble()))
                .icon(this._maker)
                .title("Station n°" + station.id)
                .snippet("Buses: " + station.buses + "\n" + "Distance: " + station.distance)
            )

            this._markers.add(marker)
        }
    }

    private fun redrawStationMarkers() {
        for (marker in this._markers) {
            marker?.remove()
        }

        this._markers = ArrayList()
        createStationsMarkers()
    }
}