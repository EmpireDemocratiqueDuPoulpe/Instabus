package com.eddp.busapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.eddp.busapp.camera.CameraHandler

class CameraActivity : AppCompatActivity() {
    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Ask for permissions
        _requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permission ->
                for (p in permission.entries) {
                    if (!p.value) {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setTitle(getString(R.string.cam_permission_refused_title))
                        alertDialog.setMessage(String.format(
                            getString(R.string.cam_permission_refused),
                            getString(R.string.app_name))
                        )

                        alertDialog.setPositiveButton("Ok") { _, _ ->
                            val intent = Intent(this@CameraActivity, MainActivity::class.java)
                            startActivity(intent)
                        }

                        alertDialog.show()
                    }
                }
            }

        askCameraPermission()
    }

    // Permission
    private fun askCameraPermission() {
        val missingPermissions: MutableList<String> = ArrayList()

        for (permission in REQUIRED_PERMISSIONS) {
            when {
                ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED -> {
                    missingPermissions.add(permission)
                }
            }
        }

        _requestPermissionLauncher.launch(missingPermissions.toTypedArray())
    }

    fun allPermissionGranted() : Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
    }
}