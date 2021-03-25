package com.chheese.app.HeadphoneToolbox.util

import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.getSystemService
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.isIgnoringBatteryOptimizations(): Boolean {
    val powerManager = getSystemService<PowerManager>()
    powerManager ?: return true
    return powerManager.isIgnoringBatteryOptimizations(packageName)
}

fun Context.isAllPermissionsGranted() =
    isIgnoringBatteryOptimizations()
            && Settings.canDrawOverlays(this)

/**
 * 检查是否已经授予所有权限
 */
inline fun Context.checkPermissions(
    permissionAllGranted: () -> Unit,
    crossinline onPositiveButtonClick: () -> Unit,
    crossinline onNegativeButtonClick: () -> Unit,
    noinline onNeutralButtonClick: (() -> Unit)? = null
) {
    if (isAllPermissionsGranted()) {
        permissionAllGranted()
        return
    }
    // 没有获得所有权限，询问用户是否进行授权
    val builder = MaterialAlertDialogBuilder(this)
        .setTitle("缺少权限")
        .setMessage("有必要权限未授予，是否前往授权管理页面？")
        .setPositiveButton("是") { _, _ ->
            onPositiveButtonClick()
        }
        .setCancelable(false)
        .setNegativeButton("忽略") { _, _ ->
            onNegativeButtonClick()
        }

    if (onNeutralButtonClick != null) {
        builder.setNeutralButton("退出") { _, _ ->
            onNeutralButtonClick()
        }
    }

    builder.create().show()
}