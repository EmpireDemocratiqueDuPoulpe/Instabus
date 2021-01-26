package com.eddp.busapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {
    private lateinit var _toolbar: Toolbar

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }

        initToolbar()
    }

    private fun initToolbar() {
        this._toolbar = findViewById(R.id.toolbar)
        this._toolbar.title = getString(R.string.settings_toolbar_title)
        setSupportActionBar(this._toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // Navigation
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var _sharedPrefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Listen for preferences change
        this._sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
        this._sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this._sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "theme" -> {
                setTheme()
            }
        }
    }

    private fun setTheme() {
        when (this._sharedPrefs.getString("theme", "light")) {
            "light" -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) }
            "dark" -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) }
            "else" -> { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }
        }

        activity?.recreate()
    }
}