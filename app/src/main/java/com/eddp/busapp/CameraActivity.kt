package com.eddp.busapp

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar

class CameraActivity : AppCompatActivity() {
    private var _stationId: Long = Long.MIN_VALUE
    private lateinit var _toolbar: Toolbar

    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    // Getters
    fun getStationId() : Long = this._stationId

    // Views
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Get station id
        val id = intent.extras?.getLong("station_id")

        if (id != null) {
            this._stationId = id
        }

        this._toolbar = findViewById(R.id.toolbar)
        this._toolbar.title = getString(R.string.cam_actionbar_title)
        setSupportActionBar(this._toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        // Ask for permissions
        this._requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                for (p in permissions.entries) {
                    if (!p.value) {
                        MainActivity.getMissingPermissionDialog(
                            this@CameraActivity,
                            getString(R.string.cam_permission_refused_title),
                            String.format(
                                getString(R.string.cam_permission_refused),
                                getString(R.string.app_name)),
                            getString(R.string.perms_refused_positive_btn)) { _, _ ->

                                val intent = Intent(this@CameraActivity, MainActivity::class.java)
                                startActivity(intent)
                        }.show()
                    }
                }
            }

        val missingPermissions = MainActivity.getMissingPermissions(this, REQUIRED_PERMISSIONS)

        if (missingPermissions != null) {
            this._requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
    }
}