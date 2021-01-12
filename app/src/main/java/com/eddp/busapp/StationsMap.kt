package com.eddp.busapp

import android.Manifest
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.eddp.busapp.data.Station
import com.eddp.busapp.interfaces.NeedStations

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class StationsMap : Fragment(), NeedStations {
    private lateinit var _activity: MainActivity

    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var _stations: List<Station> = listOf()

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

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
                                getString(R.string.app_name)),
                            getString(R.string.perms_refused_positive_btn)) { _, _ -> }.show()
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

        // Get data if it's already fetched
        val stations: List<Station>? = this._activity.getStations()

        if (stations != null) {
            this._stations = stations
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
        mapFragment?.getMapAsync(callback)
    }

    override fun getStations(stations: List<Station>) {
    }

    companion object {
        val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}