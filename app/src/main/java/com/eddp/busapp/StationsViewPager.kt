package com.eddp.busapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.eddp.busapp.data.Station
import com.eddp.busapp.interfaces.AsyncDataObserver
import com.eddp.busapp.interfaces.NeedStations
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StationsViewPager : Fragment(), AsyncDataObserver {
    private lateinit var _activity: MainActivity

    private lateinit var _viewPager2: ViewPager2
    private lateinit var _viewPagerAdapter: StationsViewPagerAdapter
    private lateinit var _tabsLayout: TabLayout

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Add observer
        this._activity = activity as MainActivity
        this._activity.registerReceiver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_stations_view_pager, container, false)

        // View Pager
        this._viewPager2 = v.findViewById(R.id.stations_view_pager)
        this._viewPagerAdapter = StationsViewPagerAdapter(activity as AppCompatActivity)
        this._viewPager2.adapter = this._viewPagerAdapter
        this._viewPager2.isUserInputEnabled = false

        // Tabs
        this._tabsLayout = v.findViewById(R.id.stations_tabs)

        TabLayoutMediator(this._tabsLayout, this._viewPager2) { tab, position ->
            tab.text = this._viewPagerAdapter.getNameAt(position)
        }.attach()

        return v
    }

    // Observer
    override fun onDataGet() {
        val stations: List<Station>? = this._activity.getStations()

        if (stations != null) {
            for (frag in this._viewPagerAdapter.getAll()) {
                (frag as NeedStations).setStations(stations)
            }
        }
    }
}

class StationsViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    private val _stationsFragments: List<Fragment> = listOf(StationsMap(), StationsList())
    private val _stationsFragmentsNames: List<String> = listOf(
        activity.getString(R.string.tabs_stations_map),
        activity.getString(R.string.tabs_stations_list),
    )

    fun getAll() : List<Fragment> = this._stationsFragments
    fun getAt(position: Int) : Fragment = this._stationsFragments[position]
    fun getNameAt(position: Int) : String = this._stationsFragmentsNames[position]

    override fun getItemCount(): Int {
        return this._stationsFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return this._stationsFragments[position]
    }
}