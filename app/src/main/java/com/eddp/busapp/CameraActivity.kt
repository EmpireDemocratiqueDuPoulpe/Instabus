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

    private var _camera: CameraHandler? = null
    private var _cameraPreviewAPI16: FrameLayout? = null
    private lateinit var _cameraButton: ImageButton

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

        // Start camera
        _cameraPreviewAPI16 = if (!CameraHandler.canUseCameraX())
            findViewById(R.id.cam_preview) else null

        this._cameraButton = findViewById(R.id.cam_capture_button)
    }

    override fun onResume() {
        super.onResume()
        this._camera = CameraHandler.getInstance(this)

        if (allPermissionGranted()) {
            this._camera?.startCamera()

            if (!CameraHandler.canUseCameraX()) {
                this._camera?.setPreviewContainer(this._cameraPreviewAPI16 as ViewGroup)
            }

            this._cameraButton.setOnClickListener { this._camera?.takePhoto() }
        }
    }

    override fun onPause() {
        super.onPause()
        this._camera?.destroy()
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

    private fun allPermissionGranted() : Boolean = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS: List<String> = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )
    }
}