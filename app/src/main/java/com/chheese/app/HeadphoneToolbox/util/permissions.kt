package com.chheese.app.HeadphoneToolbox.util

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService

fun Context.isIgnoringBatteryOptimizations(): Boolean {
    val powerManager = getSystemService<PowerManager>()
    powerManager ?: return true
    return powerManager.isIgnoringBatteryOptimizations(packageName)
}
