package com.eddp.instabus

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.eddp.instabus.data.*
import com.eddp.instabus.interfaces.AsyncDataObservable
import com.eddp.instabus.interfaces.AsyncDataObserver
import com.eddp.instabus.interfaces.WebServiceReceiver
import com.eddp.instabus.views.StationNavDrawer
import com.eddp.instabus.views.StationNavDrawerLayout
import com.eddp.instabus.views.ZoomOutPageTransformer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity(), AsyncDataObservable, WebServiceReceiver {
    private lateinit var _sharedPrefs: SharedPreferences
    private var _betterUI: Boolean = true

    private val _observers: MutableList<AsyncDataObserver?> = ArrayList()

    private lateinit var _toolbar: Toolbar
    private lateinit var _drawerLayout: StationNavDrawerLayout
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
        this._sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        this._webServiceLink = WebServiceLink(this)

        initTheme()
        authenticateUser()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        res = resources

        // Init view
        initDrawer()
        initBottomNavBar()
        initViewPager()

        // Get data
        this._webServiceLink?.getPosts()

        getStationsFromAPI()
    }

    override fun onResume() {
        super.onResume()
        this._betterUI = this._sharedPrefs.getBoolean("better_ui", true)

        setUiMode(this._betterUI)
    }

    // Todo: Update theme
    private fun initTheme() {
        when (this._sharedPrefs.getString("theme", "light")) {
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

        this._drawerLayout.addDrawerListener (object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) { }

            override fun onDrawerOpened(drawerView: View) { }

            override fun onDrawerClosed(drawerView: View) {
                closeStationDrawer()
            }

            override fun onDrawerStateChanged(newState: Int) { }
        })
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

    fun setUiMode(betterUi: Boolean) {
        if (betterUi) {
            this._bottomNavBar.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_SELECTED)
        } else {
            this._bottomNavBar.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED)
        }
    }

    // Navigation
    fun openStationDrawer(station: Station) {
        this._drawer.setStation(station)
        this._drawerLayout.openDrawer(GravityCompat.END, true)
    }

    fun closeStationDrawer() {
        this._drawerLayout.closeDrawer(GravityCompat.END)
        this._drawer.emptyUserPics()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toolbar_menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.toolbar_menu_refresh -> {
                reloadPosts()
                reloadStations()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
                false
            }
        }
    }

    private fun goToAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
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

    fun reloadPosts() {
        this._posts = ArrayList()
        this._webServiceLink?.getPosts()
    }

    fun reloadStations() {
        this._stations = ArrayList()
        getStationsFromAPI()
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
    private fun authenticateUser() {
        if (intent.getBooleanExtra("loginFromCredentials", false)) return

        val selector: String = this._sharedPrefs.getString("selector", "") ?: ""
        val authToken: String = this._sharedPrefs.getString("auth_token", "") ?: ""

        if (selector.isNotBlank()) {
            this._webServiceLink?.loginUserWithAuthToken(selector, authToken)
        } else {
            goToAuthActivity()
        }
    }

    override fun setPosts(posts: List<Post>?) {
        this._posts = posts
        notifyGet()
    }

    @SuppressLint("ApplySharedPref")
    override fun onLogin(loggedIn: Boolean, selector: String, authToken: String, userId: Int, username: String, err: String) {
        super.onLogin(loggedIn, selector, authToken, userId, username, err)

        if (!loggedIn) {
            goToAuthActivity()
        } else {
            // Save auth token
            this._sharedPrefs.edit()
                .putString("selector", selector)
                .putString("auth_token", authToken)
                .putInt("user_id", userId)
                .putString("username", username)
                .commit()
        }
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
            UserPage()
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

    fun setLabelVisibilityMode(labelVisibilityMode: Int) {
        this._bottomNavBar.labelVisibilityMode = labelVisibilityMode
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