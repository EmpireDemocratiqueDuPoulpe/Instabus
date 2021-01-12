package com.eddp.busapp

import android.Manifest
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.eddp.busapp.data.Station
import com.eddp.busapp.interfaces.NeedStations
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class StationsMap : Fragment(), NeedStations {
    private lateinit var _activity: MainActivity

    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    private var _stations: List<Station> = listOf()

    private var _googleMap: GoogleMap? = null

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
        _googleMap = googleMap

        val barcelona = LatLng(41.404377, 2.175471)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(barcelona))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 16.0f))
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(MapStyleOptions(_activity.resources
                .getString(R.string.map_style)))
            if (!success) {
                Log.e("MAP", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MAP", "Can't find style. Error: ", e)
        }
        createStationsMarkers()
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

        // Get data if it's already fetched
        val stations: List<Station>? = this._activity.getStations()

        if (stations != null) {
            this._stations = stations
            createStationsMarkers()
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
        this._stations = stations
        createStationsMarkers()
    }

    fun createStationsMarkers(){
        if(_googleMap == null) return
        val markerIcon: BitmapDescriptor = getMarkerIconFromDrawable(
            this._activity.resources.getDrawable(R.drawable.ic_bus_marker)
        )
        for (station in this._stations){
            if(station.latitude == null || station.longitude == null) continue
            _googleMap!!.addMarker(
                MarkerOptions()
                    .position(LatLng(station.latitude!!.toDouble(), station.longitude!!.toDouble()))
                    .title("Station n°" + station.id)
                    .snippet("Buses: " + station.buses + "\n" + "Distance: " + station.distance)
                    .icon(markerIcon)
            )
        }
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        );

        canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight);
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    companion object {
        val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}