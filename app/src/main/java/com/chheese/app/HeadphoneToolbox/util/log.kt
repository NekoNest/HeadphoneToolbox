package com.chheese.app.HeadphoneToolbox.util

import android.util.Log

class Logger(private val tag: String) {
    fun info(msg: String) = Log.i(tag, msg)
    fun debug(msg: String) = Log.d(tag, msg)
    fun error(msg: String) = Log.e(tag, msg)
    fun warn(msg: String) = Log.w(tag, msg)
    fun verbose(msg: String) = Log.v(tag, msg)
}

val Any.logger
    get() = Logger(this::class.java.name)