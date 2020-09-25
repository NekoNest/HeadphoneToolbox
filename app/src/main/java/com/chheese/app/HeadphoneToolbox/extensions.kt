package com.chheese.app.HeadphoneToolbox

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Message
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

fun newMessage(what: Int, arg1: Int = -1): Message {
    val message = Message.obtain()
    message.what = what
    if (arg1 != -1) {
        message.arg1 = arg1
    }
    return message
}