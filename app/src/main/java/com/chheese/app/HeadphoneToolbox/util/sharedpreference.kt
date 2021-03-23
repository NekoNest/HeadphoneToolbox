package com.chheese.app.HeadphoneToolbox.util

import android.content.SharedPreferences

@Suppress("UNCHECKED_CAST")
fun <T> SharedPreferences.get(pref: String, defaultValue: T): T {
    val value = all[pref] ?: return defaultValue
    return value as T
}

fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    block(editor)
    editor.apply()
}

object PreferenceKeys {
    const val CATEGORY_OTHER = "category_other"
    const val CATEGORY_PLAYER_SETTINGS = "player_settings"
    const val CATEGORY_EXPERIMENTAL_FEATURES = "experimental_features"
    const val CATEGORY_ABOUT = "category_about"
    const val CATEGORY_DEVELOPERS = "category_developers"

    const val PREF_SELECT_PLAYER = "select_player"
    const val PREF_VIEW_LOG = "view_log"
    const val PREF_OPEN_DETAILS = "open_details"
    const val PREF_OPEN_IN_COOLAPK = "open_in_coolapk"
    const val PREF_ABOUT = "about"
    const val PREF_ABOUT_AUTHOR = "about_author"
    const val PREF_NEW_UI_SETTINGS = "new_ui_settings"
    const val PREF_CHECK_FOR_UPDATES = "check_for_updates"
    const val PREF_RESTART_UI = "restart_ui"

    const val INT_CORNER_SIZE_TOP_START = "corner_size_top_start"
    const val INT_CORNER_SIZE_TOP_END = "corner_size_top_end"
    const val INT_CORNER_SIZE_BOTTOM_START = "corner_size_bottom_start"
    const val INT_CORNER_SIZE_BOTTOM_END = "corner_size_bottom_end"

    const val STRING_CORNER_TYPE = "corner_type"
    const val STRING_THEME_COLOR_PRIMARY = "theme_color_primary"

    const val SWITCH_LIGHT_SCREEN = "light_screen"
    const val SWITCH_ALERT_ON_OPEN = "alert_on_open"
    const val SWITCH_OPEN_PLAYER = "open_player"
    const val SWITCH_ALLOW_PARALLEL = "allow_parallel"
    const val SWITCH_ALLOW_BLUETOOTH = "allow_bluetooth"
    const val SWITCH_USE_EXPERIMENTAL_FEATURE = "use_experimental_feature"
    const val SWITCH_DISABLE_FREE_APP_DIALOG = "disable_free_app_dialog"
    const val SWITCH_SHOW_ALL_APPS = "show_all_apps"
    const val SWITCH_USE_NEW_UI = "use_new_ui"
}