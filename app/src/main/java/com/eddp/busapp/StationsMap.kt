package com.eddp.busapp

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.eddp.busapp.data.Station
import com.eddp.busapp.interfaces.NeedStations
import com.eddp.busapp.views.MapReady
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class StationsMap : Fragment(), NeedStations {
    private lateinit var _activity: MainActivity

    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var _mapReady: MapReady? = null

    // Setters
    override fun setStations(stations: List<Station>) {
        this._mapReady?.setStations(stations)
    }

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this._activity = activity as MainActivity

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

        val stations: List<Station>? = this._activity.getStations()

        if (stations != null) {
            this._mapReady?.setStations(stations)
        }
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

    companion object {
        val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}