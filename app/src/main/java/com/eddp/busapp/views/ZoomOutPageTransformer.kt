package com.eddp.busapp.views

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

private const val MIN_SCALE = 0.85f
private const val MIN_ALPHA = 0.5f

class ZoomOutPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            val pageWidth = width
            val pageHeight = height

            when {
                position < -1 -> { alpha = 0f }
                position <= 1 -> {
                    val scaleFactor = MIN_SCALE.coerceAtLeast(1 - abs(position))
                    val verticalMargin = pageHeight * (1 - scaleFactor) / 2
                    val horizontalMargin = pageWidth * (1 - scaleFactor) / 2

                    translationX = if (position < 0) {
                        horizontalMargin - verticalMargin / 2
                    } else {
                        horizontalMargin + verticalMargin / 2
                    }

                    // Change page scale
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Change the page alpha depending on its size
                    alpha = (MIN_ALPHA + (((scaleFactor - MIN_ALPHA) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                }
                else -> { alpha = 0f }
            }
        }
    }
}