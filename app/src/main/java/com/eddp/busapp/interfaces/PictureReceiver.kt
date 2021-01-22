package com.eddp.busapp.interfaces

import android.net.Uri

interface PictureReceiver {
    fun onPictureSaved(path: Uri)
}