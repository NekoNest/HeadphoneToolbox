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

object PreferenceKeys {
    const val SWITCH_OPEN_PLAYER = "open_player"
    const val CATEGORY_PLAYER_SETTINGS = "player_settings"
    const val SWITCH_ALERT_ON_OPEN = "alert_on_open"
    const val PREF_SELECT_PLAYER = "select_player"
    const val PREF_VIEW_LOG = "view_log"
    const val PREF_OPEN_DETAILS = "open_details"
    const val PREF_OPEN_IN_COOLAPK = "open_in_coolapk"
    const val PREF_ABOUT = "about"
    const val SWITCH_ALLOW_PARALLEL = "allow_parallel"
    const val SWITCH_USE_EXPERIMENTAL_FEATURE = "use_experimental_feature"
    const val CATEGORY_EXPERIMENTAL_FEATURES = "experimental_features"
    const val SWITCH_USE_NEW_UI = "use_new_ui"
    const val PREF_ABOUT_AUTHOR = "about_author"
}