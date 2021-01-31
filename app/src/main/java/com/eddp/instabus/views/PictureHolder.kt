package com.eddp.instabus.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.eddp.instabus.R
import java.io.InputStream
import java.lang.Exception

class PictureHolder constructor(context: Context, attrs: AttributeSet?, defStyle: Int)
    : ConstraintLayout(context, attrs, defStyle) {
    private val _context = context

    private lateinit var _progressBar: ProgressBar
    private lateinit var _imageView: ImageView

    private var _imgPath = ""
    private var _retry = false
    private var _bitmap: Bitmap? = null

    init {
        initView(attrs)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    // Views
    private fun initView(attrs: AttributeSet?) {
        View.inflate(context, R.layout.picture_holder, this)

        // Get custom args
        val typedArr: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PictureHolder)

        try {
            // GET
        } finally {
            typedArr.recycle()
        }

        // Get view
        this._progressBar = findViewById(R.id.picture_holder_loading)
        this._imageView = findViewById(R.id.picture_holder_img)
    }

    // Image loading
    fun setPath(path: String) : PictureHolder {
        this._imgPath = path
        return this
    }

    fun isLoading(isLoading: Boolean) : PictureHolder {
        if (isLoading) {
            this._progressBar.visibility = View.VISIBLE
            this._imageView.visibility = View.INVISIBLE
        } else {
            this._progressBar.visibility = View.INVISIBLE
            this._imageView.visibility = View.VISIBLE
        }

        return this
    }

    fun retryOnError(retry: Boolean) : PictureHolder {
        this._retry = retry
        return this
    }

    fun addRawFallback(resId: Int) : PictureHolder {
        try {
            val inputStream: InputStream = _context.resources.openRawResource(resId)
            this._bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (err: Exception) {}

        return this
    }

    fun load() {
        isLoading(true)

        Glide.with(context)
            .load(this._imgPath)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .listener(PictureLoaderListener())
            .into(this._imageView)
    }

    inner class PictureLoaderListener : RequestListener<Drawable> {
        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            isLoading(false)
            return false
        }

        override fun onLoadFailed(
            err: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            Log.e("Picasso", "Error while loading image: ${err?.message}", err)

            if (_retry) {
                Handler(Looper.getMainLooper()).postDelayed({
                    load()
                }, 1000)
            } else {
                if (_bitmap != null) {
                    isLoading(false)
                    Handler(Looper.getMainLooper()).postDelayed({
                        _imageView.setImageBitmap(_bitmap)
                    }, 1000)
                }
            }

            return false
        }
    }
}