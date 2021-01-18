package com.eddp.busapp.views.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eddp.busapp.MainActivity
import com.eddp.busapp.R
import com.eddp.busapp.data.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationAdapter(activity: MainActivity) : ListAdapter<Station, RecyclerView.ViewHolder>(StationDiffCallback()) {
    private val _activity = activity

    private val _adapterCoroutine = CoroutineScope(Dispatchers.Default)

    // Setters
    fun setData(list: List<Station>){
        this._adapterCoroutine.launch {
            withContext(Dispatchers.Main){
                submitList(list)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StationViewHolder.from(this._activity, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StationViewHolder -> {
                val station = getItem(position) as Station
                holder.bind(station)
            }
        }
    }
}

class StationDiffCallback : DiffUtil.ItemCallback<Station>() {
    override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem == newItem
    }
}

class StationViewHolder(activity: MainActivity, view: View) : RecyclerView.ViewHolder(view)  {
    private val _activity = activity
    private val _v = view

    fun bind(station: Station) {
        this._v.findViewById<TextView>(R.id.station_name).text = station.concatName
        this._v.findViewById<TextView>(R.id.station_street_name).text = station.streetName
        this._v.findViewById<TextView>(R.id.station_city).text = station.city

        this._v.findViewById<TextView>(R.id.view_station_btn).setOnClickListener {
            this._activity.openStationDrawer(station)
        }
    }

    companion object {
        fun from(activity: MainActivity, parent: ViewGroup) : StationViewHolder {
            val v: View = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.station_recycler_view_item, parent, false)

            return StationViewHolder(activity, v)
        }
    }
}