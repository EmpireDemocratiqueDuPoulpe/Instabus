package com.eddp.busapp

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import com.eddp.busapp.camera.CameraHandler
import com.eddp.busapp.interfaces.PictureReceiver

class TakePicture : Fragment(), PictureReceiver {
    private var _context: Context? = null

    private var _camera: CameraHandler? = null
    private var _cameraPreviewAPI16: FrameLayout? = null
    private lateinit var _cameraButton: ImageButton

    // Views
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_take_picture, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as CameraActivity).showShareBtn(false)
        initCamPreview(view)
    }

    private fun initCamPreview(view: View) {
        this._cameraPreviewAPI16 = if (!CameraHandler.canUseCameraX())
            view.findViewById(R.id.cam_preview) else null

        this._cameraButton = view.findViewById(R.id.cam_capture_button)
    }

    // Resume / Pause
    override fun onResume() {
        super.onResume()
        this._context = activity as Context

        if (this._context != null) {
            this._camera = CameraHandler.getInstance(this._context!!)
            this._camera?.registerReceiver(this)

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

    // Picture receiver
    override fun onPictureSaved(path: Uri) {
        val fragment = AddPicture()
        val args = Bundle()

        args.putString("img_path", path.toString())
        fragment.arguments = args

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.cam_nav_host_fragment, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }
}