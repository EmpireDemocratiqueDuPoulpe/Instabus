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

    companion object {
        val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
    }
}