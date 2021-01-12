package com.eddp.busapp.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.eddp.busapp.R
import com.eddp.busapp.UserPics

class ViewStationClickListener(
    supportFragmentManager: FragmentManager,
    stationId: Long
) : View.OnClickListener {
    private val _supportFragmentManager = supportFragmentManager
    private val _stationId = stationId

    override fun onClick(v: View?) {
        val userPics = UserPics()
        val args = Bundle()

        args.putLong("station_id", this._stationId)
        userPics.arguments = args

        this._supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_view_pager, userPics)
            .addToBackStack(null)
            .commit()
    }
}