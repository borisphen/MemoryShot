package com.borisphen.memoryshot.util.platform

import android.util.Log

fun Any.log(text: String) {
    Log.w(this.javaClass.simpleName, "-|-|- $text")
}