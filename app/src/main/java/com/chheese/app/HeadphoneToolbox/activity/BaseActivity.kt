package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.gyf.immersionbar.ktx.immersionBar

open class BaseActivity : AppCompatActivity() {
    protected lateinit var app: HeadphoneToolbox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as HeadphoneToolbox
        immersionBar {
            transparentStatusBar()
            navigationBarColor(android.R.color.white)
            navigationBarDarkIcon(true)
            statusBarDarkFont(true)
            fitsSystemWindows(true)
        }
    }

    companion object {
        const val SHOW_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 0x005
    }
}