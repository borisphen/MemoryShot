package com.borisphen.memoryshot.util.ui

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager

fun Context.getScreenBounds(): Rect {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        wm.currentWindowMetrics.bounds
    } else {
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        Rect(0, 0, metrics.widthPixels, metrics.heightPixels)
    }
}