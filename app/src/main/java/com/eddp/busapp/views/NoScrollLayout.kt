package com.eddp.busapp.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapView

class NoScrollLayout : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, i: Int) : super(context, attr, i)
    //constructor(context: Context, options: GoogleMapOptions) : super(context, options)

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }
}