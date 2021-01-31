package com.eddp.instabus

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {
    private lateinit var _toolbar: Toolbar

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
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

class SettingsFragment
    : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {
    private lateinit var _sharedPrefs: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Listen for preferences change
        this._sharedPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
        this._sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        // Log out
        val logOutBtn: Preference? = findPreference("log_out")

        logOutBtn?.summary = String.format(
            activity?.getString(R.string.log_out_summary) ?: "",
            this._sharedPrefs.getString("username", "")
        )
        logOutBtn?.onPreferenceClickListener = this
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

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            "log_out" -> {
                logOut()
                true
            }
            else -> {
                false
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

    @SuppressLint("ApplySharedPref")
    private fun logOut() {
        this._sharedPrefs.edit()
            .putString("selector", "")
            .putString("auth_token", "")
            .putInt("user_id", Int.MIN_VALUE)
            .putString("username", "")
            .commit()

        startActivity(Intent(activity, AuthActivity::class.java))
        activity?.finish()
    }
}