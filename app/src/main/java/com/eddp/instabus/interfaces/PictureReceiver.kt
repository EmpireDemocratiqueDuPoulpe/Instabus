package com.eddp.instabus.interfaces

import android.net.Uri

interface PictureReceiver {
    fun onPictureSaved(path: Uri)
}