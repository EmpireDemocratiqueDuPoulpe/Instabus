package com.eddp.instabus.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import com.eddp.instabus.R
import java.io.IOException
import java.lang.Exception

@SuppressLint("ViewConstructor")
class CamPreviewAPI16(
    context: Context,
    private val _cam: Camera
    ) : SurfaceView(context), SurfaceHolder.Callback {

    private val _context = context
    private val _holder: SurfaceHolder = holder.apply {
        addCallback(this@CamPreviewAPI16)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        _cam.apply {
            var errMsg: String? = null

            try {
                setPreviewDisplay(holder)
                startPreview()
            } catch (err: IOException) {
                errMsg = _context.getString(R.string.cam_start_preview_error)
            } catch (err: Exception) {
                errMsg = _context.getString(R.string.cam_unknown_error)
            } finally {
                if (errMsg != null) {
                    Toast.makeText(
                        _context,
                        errMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) { }

    override fun surfaceDestroyed(holder: SurfaceHolder) { }
}