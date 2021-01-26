package com.eddp.busapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class Settings : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
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
                activity?.recreate()
            }
            "better_ui" -> {
                val betterUI = this._sharedPrefs.getBoolean("better_ui", true)

                if (activity is MainActivity) {
                    (activity as MainActivity).setUiMode(betterUI)
                }
            }
        }
    }
}