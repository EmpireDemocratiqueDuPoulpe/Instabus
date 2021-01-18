package com.eddp.busapp

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.eddp.busapp.data.*
import com.eddp.busapp.interfaces.AsyncDataObservable
import com.eddp.busapp.interfaces.AsyncDataObserver
import com.eddp.busapp.interfaces.WebServiceReceiver
import com.eddp.busapp.views.StationNavDrawer
import com.eddp.busapp.views.ZoomOutPageTransformer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity(), AsyncDataObservable, WebServiceReceiver {
    private val _observers: MutableList<AsyncDataObserver?> = ArrayList()

    private lateinit var _toolbar: Toolbar
    private lateinit var _drawerLayout: DrawerLayout
    private lateinit var _drawer: StationNavDrawer
    private lateinit var _viewPager: ViewPager2
    private lateinit var _viewPagerAdapter: MainViewPagerAdapter
    private lateinit var _bottomNavBar: BottomNavBar

    private var _webServiceLink: WebServiceLink? = null

    private var _posts: List<Post>? = null
    private var _stations: List<Station>? = null

    // Getters
    fun getViewPager() : ViewPager2 = this._viewPager

    fun getPosts() : List<Post>? = this._posts
    fun getStations() : List<Station>? = this._stations

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        initTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        res = resources

        // Init view
        initDrawer()
        initBottomNavBar()
        initViewPager()

        // Get data
        this._webServiceLink = WebServiceLink.getInstance(this)
        this._webServiceLink?.getPosts()

        getStationsFromAPI()
    }

    // Todo: Update theme
    private fun initTheme() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPrefs.getString("theme", "light")) {
            "light" -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
            "dark" -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
            "else" -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
        }
    }

    private fun initDrawer() {
        this._toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(this._toolbar)

        this._drawerLayout = findViewById(R.id.drawer_layout)
        this._drawer = findViewById(R.id.drawer)
    }

    private fun initBottomNavBar() {
        this._bottomNavBar = BottomNavBar(this)
    }

    private fun initViewPager() {
        this._viewPager = findViewById(R.id.main_view_pager)
        this._viewPagerAdapter = MainViewPagerAdapter(this)
        this._viewPager.adapter = this._viewPagerAdapter
        this._viewPager.registerOnPageChangeCallback(MainViewPagerCallback(this._bottomNavBar))
        this._viewPager.setPageTransformer(ZoomOutPageTransformer())

        setViewPager2Sensitivity(4)
    }

    // Navigation
    fun openStationDrawer(station: Station) {
        this._drawer.setStation(station)
        this._drawerLayout.openDrawer(GravityCompat.END, true)
    }

    fun closeStationDrawer(item: UserPic) {
        this._drawerLayout.closeDrawer(GravityCompat.END)

        // Pressed user pic
    }

    override fun onBackPressed() {
        if (this._viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            this._viewPager.currentItem = this._viewPager.currentItem - 1
        }
    }

    fun setViewPager2Sensitivity(multiplier: Int) {
        val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        recyclerViewField.isAccessible = true
        val recyclerView = recyclerViewField.get(this._viewPager) as RecyclerView

        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true

        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * multiplier)
    }

    // Async data
    private fun getStationsFromAPI() {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("http://barcelonaapi.marcpous.com")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        val service = retrofit.create(StationAPI::class.java)
        val call = service.stationsList()

        call.enqueue(object : Callback<StationResponse> {
            override fun onResponse(
                    call: Call<StationResponse>,
                    response: Response<StationResponse>
            ) {
                val statusCode: Int = response.code()
                val resp: StationResponse? = response.body()

                if (!response.isSuccessful) {
                    Toast.makeText(
                            this@MainActivity,
                            "Error ${statusCode}: " + this@MainActivity.getString(R.string.get_stations_error),
                            Toast.LENGTH_SHORT
                    ).show()
                } else {
                    this@MainActivity._stations = resp?.data?.stations
                    notifyGet()
                }
            }

            override fun onFailure(call: Call<StationResponse>?, err: Throwable) {
                Toast.makeText(
                        this@MainActivity,
                        this@MainActivity.getString(R.string.get_stations_error),
                        Toast.LENGTH_SHORT
                ).show()
                Log.e("ERROR", err.message, err)
            }
        })
    }

    // Observable
    override fun registerReceiver(dataObserver: AsyncDataObserver) {
        if (!this._observers.contains(dataObserver)) {
            this._observers.add(dataObserver)
        }
    }

    override fun unregisterReceiver(dataObserver: AsyncDataObserver) {
        if (!this._observers.contains(dataObserver)) {
            this._observers.remove(dataObserver)
        }
    }

    override fun notifyGet() {
        for (observer in this._observers) {
            observer?.onDataGet()
        }
    }

    // Web Service
    override fun setPosts(posts: List<Post>?) {
        this._posts = posts
        notifyGet()
    }

    companion object {
        // Resources
        private var res: Resources? = null

        fun getResources() : Resources? = res

        // Permissions
        fun allPermissionGranted(context: Context, requiredPermissions: List<String>) : Boolean {
            return requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun getMissingPermissions(
            context: Context,
            requiredPermissions: List<String>
        ) : MutableList<String>? {
            val missingPermissions: MutableList<String> = ArrayList()

            for (permission in requiredPermissions) {
                when {
                    ContextCompat.checkSelfPermission(context, permission)
                            != PackageManager.PERMISSION_GRANTED -> {
                        missingPermissions.add(permission)
                    }
                }
            }

            return if (missingPermissions.size > 0)
                missingPermissions else null
        }

        fun getMissingPermissionDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String,
            onPositiveButtonClick: DialogInterface.OnClickListener) : AlertDialog.Builder {

            return AlertDialog
                .Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, onPositiveButtonClick)
        }
    }
}

// Navigation
class MainViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val _fragments: List<Fragment> = listOf(
            Home(),
            StationsViewPager(),
            Settings()
    )

    fun getFragmentById(position: Int) : Fragment = this._fragments[position]

    override fun getItemCount() : Int = this._fragments.size

    override fun createFragment(position: Int) : Fragment = this._fragments[position]
}

class MainViewPagerCallback(bottomNavBar: BottomNavBar)
    : ViewPager2.OnPageChangeCallback() {
    private val _bottomNavBar = bottomNavBar

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        _bottomNavBar.setSelectedItem(position)
    }
}

class BottomNavBar(activity: MainActivity) : BottomNavigationView.OnNavigationItemSelectedListener {
    private val _activity = activity
    private val _bottomNavBar: BottomNavigationView = this._activity.findViewById(R.id.bottom_nav_bar)

    private val _menu: Menu = this._bottomNavBar.menu
    private val _items: MutableList<MenuItem> = ArrayList()
    private var _previousSelectedMenu: Int = 0

    init {
        for (i in 0 until this._menu.size())  {
            this._items.add(this._menu.getItem(i))
        }

        // Add events
        this._bottomNavBar.setOnNavigationItemSelectedListener(this)
    }

    // Setters
    fun setSelectedItem(position: Int) {
        updatePreviousSelectedItem()
        this._items[position].isChecked = true
    }

    private fun updatePreviousSelectedItem() {
        this._previousSelectedMenu = this._bottomNavBar.selectedItemId
    }

    // Navigation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Get current item pos
        val itemPos = this._items.indexOf(item)
        if (this._previousSelectedMenu == itemPos) return false

        // Update
        this._activity.getViewPager().setCurrentItem(itemPos, true)
        updatePreviousSelectedItem()

        return true
    }
}