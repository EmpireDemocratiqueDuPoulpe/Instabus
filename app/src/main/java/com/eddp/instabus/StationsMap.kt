package com.eddp.instabus

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.eddp.instabus.data.Station
import com.eddp.instabus.data.StationAPILink
import com.eddp.instabus.interfaces.NeedStations
import com.eddp.instabus.interfaces.StationAPIReceiver
import com.eddp.instabus.views.MapReady
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class StationsMap : Fragment(), StationAPIReceiver {
    private lateinit var _activity: MainActivity

    private lateinit var _stationAPILink: StationAPILink
    private var _stations: List<Station> = ArrayList()

    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var _mapReady: MapReady? = null
    private lateinit var _locationManager: LocationManager
    private lateinit var _locationListener: StationLocationListener
    private var _timeBeforeUpdate: Long = 5000
    private var _distanceBeforeUpdate: Float = 100f

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this._activity = activity as MainActivity
        this._stationAPILink = StationAPILink(this)

        // Ask permission
        this._requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                for (p in permissions.entries) {
                    if (!p.value) {
                        MainActivity.getMissingPermissionDialog(
                            this@StationsMap.requireContext(),
                            getString(R.string.map_permission_refused_title),
                            String.format(
                                getString(R.string.map_permission_refused),
                                getString(R.string.app_name)
                            ),
                            getString(R.string.perms_refused_positive_btn)
                        ) { _, _ -> }.show()
                    }
                }
            }

        val missingPermissions = MainActivity.getMissingPermissions(
            requireContext(),
            REQUIRED_PERMISSIONS
        )

        if (missingPermissions != null) {
            this._requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        }

        // Init Google Maps
        this._locationManager = this._activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        this._locationListener = StationLocationListener {lat, lon -> this._stationAPILink.getStationsNear(lat, lon) }
        this._locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this._timeBeforeUpdate, this._distanceBeforeUpdate, this._locationListener)
        this._locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this._timeBeforeUpdate, this._distanceBeforeUpdate, this._locationListener)

        this._mapReady = MapReady(
            // Context
            this._activity,
            // Map style
            _activity.resources.getString(R.string.map_style),
            // Custom marker
            ResourcesCompat.getDrawable(
                _activity.resources,
                R.drawable.ic_bus_marker,
                _activity.theme
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stations_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this._mapReady)
    }

    override fun setNearStations(stations: List<Station>?, err: String?) {
        super.setNearStations(stations, err)

        if (stations != null) {
            this._stations = stations
        } else {
            if (err != null) {
                Toast.makeText(this.activity, err, Toast.LENGTH_SHORT).show()
            }
        }

        this._locationListener.setIsUpdating(false)
        this._mapReady?.setStations(stations)
    }

    companion object {
        val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}

class StationLocationListener(onMovement: (lat: String, lon: String) -> Unit) : LocationListener {
    private var _onMovementCallback = onMovement
    private var _isUpdating = false

    // Getters
    fun isUpdating() : Boolean = this._isUpdating

    // Setters
    fun setIsUpdating(updating: Boolean) { this._isUpdating = updating }

    // Location
    override fun onLocationChanged(location: Location) {
        // Prevent two update at same time
        if (this._isUpdating) return
        this._isUpdating = true

        // Get latitude and longitude
        val lat: String = location.latitude.toString()
        val lon: String = location.longitude.toString()

        // Update
        this._onMovementCallback.invoke(lat, lon)
    }
}