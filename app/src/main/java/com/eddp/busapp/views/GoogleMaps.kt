package com.eddp.busapp.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.eddp.busapp.MainActivity
import com.eddp.busapp.R
import com.eddp.busapp.data.Station
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import java.lang.Exception

class MapReady(context: Context, style: String?, marker: Drawable?) : OnMapReadyCallback {
    private val _context = context

    private var _map: GoogleMap? = null
    private var _isMapReady: Boolean = false
    private var _style: String? = style
    private var _makerDrawable: Drawable? = marker
    private lateinit var _maker: BitmapDescriptor

    private var _stations: List<Station> = listOf()
    private var _markers: HashMap<Marker, Station> = HashMap()

    constructor(context: Context, style: String) : this(context, style, null)
    constructor(context: Context, marker: Drawable?) : this(context, null, marker)

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
    fun getStationByMarker(marker: Marker) : Station? {
        return try {
            this._markers[marker]
        } catch (err: Exception) {
            null
        }
    }

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
        val infoWindow = StationInfoWindow(this._context, this)
        map?.setInfoWindowAdapter(infoWindow)
        map?.setOnInfoWindowClickListener(infoWindow)

        // Update map style
        if (this._style != null) {
            try {
                val newStyle: Boolean = map?.setMapStyle(MapStyleOptions(this._style)) ?: false

                if (!newStyle) Log.e("Map", "Failed to parse style file.")
            } catch (err: Resources.NotFoundException) {
                Log.e("Map", err.message, err)
            }
        }

        this._maker = getMarkerIconFromDrawable(this._makerDrawable)

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
            )

            if (marker != null) {
                this._markers[marker] = station
            }
        }
    }

    private fun redrawStationMarkers() {
        for ((marker, _) in this._markers) {
            marker.remove()
        }

        this._markers = HashMap()
        createStationsMarkers()
    }
}

class StationInfoWindow(context: Context, m: MapReady) : GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
    private val _context = context
    private val _window: View = LayoutInflater.from(context).inflate(R.layout.station_info_window, null)
    private val _map = m

    override fun getInfoWindow(p0: Marker?): View {
        show(p0)
        return this._window
    }

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    private fun show(marker: Marker?) {
        if (marker == null) return

        val station: Station? = this._map.getStationByMarker(marker)

        if (station != null) {
            this._window.findViewById<TextView>(R.id.marker_title).text = station.concatName
            this._window.findViewById<TextView>(R.id.marker_buses).text = station.concatBuses
            this._window.findViewById<TextView>(R.id.marker_distance).text = station.concatDistance
        }
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if(marker == null) return

        val station: Station? = this._map.getStationByMarker(marker)

        if(station != null){
            if(this._context is MainActivity){
                this._context.openStationDrawer(station)
            }
        }
    }
}