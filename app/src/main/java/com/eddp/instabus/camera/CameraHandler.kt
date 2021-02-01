package com.eddp.instabus.camera

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.eddp.instabus.CameraActivity
import com.eddp.instabus.R
import com.eddp.instabus.interfaces.PictureReceiver
import com.eddp.instabus.interfaces.PictureTaker
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraHandler private constructor(context: Context) : PictureTaker {
    private val _observers: MutableList<PictureReceiver?> = ArrayList()
    private var _context = context

    private var _outputDir: File?

    // API 16+
    private var _camAPI16: Camera? = null
    private var _camPreviewAPI16: CamPreviewAPI16? = null

    // API 21+
    private var _imageCapture: ImageCapture? = null
    private var _executor: ExecutorService

    init {
        this._outputDir = getOutputDir()
        this._executor = Executors.newSingleThreadExecutor()
    }

    // Getters
    fun getOutputDir() : File? {
        this._outputDir.apply {
            if (this == null) {
                return if (canUseCameraX())
                    getOutputDirAPI21() else
                    getOutputDirAPI16()
            }
        }

        return this._outputDir
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun getOutputDirAPI16() : File? {
        val mediaDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            this._context.getString(R.string.app_name)
        )

        mediaDir.apply {
            if (!exists()) {
                if (!mkdirs()) {
                    return null
                }
            }
        }

        return mediaDir
    }

    // TODO : MediaStore instead of externalMediaDirs
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun getOutputDirAPI21() : File? {
        val mediaDir: File? = this._context.externalMediaDirs.firstOrNull().let {
            File(it, this._context.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists())
            mediaDir else this._context.filesDir
    }

    fun deviceHasCamera() : Boolean {
        return this._context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    // Setters
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun setPreviewContainer(previewContainer: ViewGroup) {
        previewContainer.addView(this._camPreviewAPI16)
    }

    // Camera functions
    fun startCamera() {
        if (canUseCameraX())
            startCameraAPI21() else
            startCameraAPI16()
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun startCameraAPI16() {
        try {
            this._camAPI16 = Camera.open()

            if (this._camAPI16 != null) {
                this._camPreviewAPI16 = CamPreviewAPI16(this._context, this._camAPI16!!)
            }
    } catch (err: Exception) {
            Log.e("DEBUG", err.message, err)
            Toast.makeText(
                this._context,
                this._context.getString(R.string.cam_start_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun startCameraAPI21() {
        val futureCamProvider = ProcessCameraProvider.getInstance(this._context)

        futureCamProvider.addListener({
            val camProvider: ProcessCameraProvider = futureCamProvider.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    //it.setSurfaceProvider(
                    //    (
                    //        (this._context as AppCompatActivity).camPreview as PreviewView
                    //    ).surfaceProvider)
                    it.setSurfaceProvider(
                        ((this._context as CameraActivity).findViewById<PreviewView>(R.id.cam_preview)).surfaceProvider
                    )
                }
            val camSelector = CameraSelector.DEFAULT_BACK_CAMERA
            this._imageCapture = ImageCapture.Builder().build()

            try {
                camProvider.unbindAll()
                camProvider.bindToLifecycle(
                    this._context as AppCompatActivity,
                    camSelector,
                    preview,
                    this._imageCapture
                )
            } catch (err: Exception) {
                Toast.makeText(
                    this._context,
                    this._context.getString(R.string.cam_start_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this._context))
    }

    fun takePhoto() {
        if (canUseCameraX())
            takePhotoAPI21() else
            takePhotoAPI16()
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun takePhotoAPI16() {
        this._camAPI16?.takePicture(null, null, Camera.PictureCallback { data, _ ->
            val photoFile = File(
                this._outputDir,
                "${SimpleDateFormat(FILENAME_FORMAT, Locale.FRENCH).format(System.currentTimeMillis())}.jpg"
            )
            var errMsg: String? = null

            try {
                val fos = FileOutputStream(photoFile)
                fos.write(data)
                fos.close()
            } catch (err: FileNotFoundException) {
                errMsg = this._context.getString(R.string.cam_file_not_found_error)
            } catch (err: IOException) {
                errMsg = this._context.getString(R.string.cam_file_access_denied_error)
            } catch (err: Exception) {
                errMsg = this._context.getString(R.string.cam_unknown_error)
            } finally {
                if (errMsg != null) {
                    Toast.makeText(
                        this._context,
                        errMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val savedUri = Uri.fromFile(photoFile)
                    notifySave(savedUri)
                }
            }
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun takePhotoAPI21() {
        val imageCapture = this._imageCapture ?: return
        val photoFile = File(
            this._outputDir,
            "${SimpleDateFormat(FILENAME_FORMAT, Locale.FRENCH).format(System.currentTimeMillis())}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this._context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    notifySave(savedUri)
                    Log.d("Camera", "Photo saved in: $savedUri")
                }

                override fun onError(err: ImageCaptureException) {
                    val errMsg = _context.getString(R.string.cam_image_capture_error)
                    Toast.makeText(_context, errMsg, Toast.LENGTH_SHORT).show()
                    Log.e("Camera", "$errMsg: ${err.message}")
                }
            }
        )
    }

    fun destroy() {
        this._executor.shutdown()
        instance = null
    }

    // Picture taker
    override fun registerReceiver(pictureReceiver: PictureReceiver) {
        if (!this._observers.contains(pictureReceiver)) {
            this._observers.add(pictureReceiver)
        }
    }

    override fun unregisterReceiver(pictureReceiver: PictureReceiver) {
        if (!this._observers.contains(pictureReceiver)) {
            this._observers.remove(pictureReceiver)
        }
    }

    override fun notifySave(path: Uri) {
        for (observer in this._observers) {
            observer?.onPictureSaved(path)
        }
    }

    companion object {
        // Singleton
        private var instance: CameraHandler? = null

        @Synchronized
        fun getInstance(context: Context) : CameraHandler? {
            if (instance == null) {
                instance = CameraHandler(context)
            }

            return instance
        }

        // Camera
        private const val FILENAME_FORMAT = "dd-MM-yyyy_HH-mm-ss-SSS"

        fun canUseCameraX() : Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        }
    }
}