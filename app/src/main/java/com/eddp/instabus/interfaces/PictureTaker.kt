package com.eddp.instabus.interfaces

import android.net.Uri

interface PictureTaker {
    fun registerReceiver(pictureReceiver: PictureReceiver)
    fun unregisterReceiver(pictureReceiver: PictureReceiver)
    fun notifySave(path: Uri)
}