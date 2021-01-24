package com.eddp.busapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserPicDrawer : Fragment(), WebServiceReceiver {
    private var _context: Context? = null

    private var _stationId: Long = Long.MIN_VALUE

    // Views
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.station_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this._context = activity
        this._stationId = view.findViewById<TextView>(R.id.drawer_station_name).tag.toString().toLong()

        initAddPost(view)
    }

    private fun initAddPost(view: View) {
        view.findViewById<FloatingActionButton>(R.id.drawer_take_picture_btn).setOnClickListener {
            val camera = Intent(this._context, CameraActivity::class.java)
            camera.putExtra("station_id", this._stationId)

            startActivity(camera)
        }
    }
}