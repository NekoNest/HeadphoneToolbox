package com.chheese.app.HeadphoneToolbox.util

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.annotation.StringRes

@Suppress("UNCHECKED_CAST")
fun <T> SharedPreferences.get(resource: Resources, @StringRes id: Int, defaultValue: T): T {
    val value = all[resource.getString(id)] ?: return defaultValue
    return value as T
}

fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    block(editor)
    editor.apply()
}
