package com.eddp.busapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import com.eddp.busapp.camera.CameraHandler

class TakePicture : Fragment() {
    private var _context: Context? = null

    private var _camera: CameraHandler? = null
    private var _cameraPreviewAPI16: FrameLayout? = null
    private lateinit var _cameraButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_take_picture, container, false)

        // Get camera component
        _cameraPreviewAPI16 = if (!CameraHandler.canUseCameraX())
            v.findViewById(R.id.cam_preview) else null

        this._cameraButton = v.findViewById(R.id.cam_capture_button)

        return v
    }

    override fun onResume() {
        super.onResume()
        this._context = activity as Context

        if (this._context != null) {
            this._camera = CameraHandler.getInstance(this._context!!)

            if (MainActivity.allPermissionGranted(this._context!!, CameraActivity.REQUIRED_PERMISSIONS)) {
                this._camera?.startCamera()

                if (!CameraHandler.canUseCameraX()) {
                    this._camera?.setPreviewContainer(this._cameraPreviewAPI16 as ViewGroup)
                }

                this._cameraButton.setOnClickListener { this._camera?.takePhoto() }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        this._camera?.destroy()
    }
}