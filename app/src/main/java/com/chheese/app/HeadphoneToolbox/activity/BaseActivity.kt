package com.chheese.app.HeadphoneToolbox.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.util.logger
import com.gyf.immersionbar.ktx.immersionBar

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var app: HeadphoneToolbox
    protected var darkMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as HeadphoneToolbox
        immersionBar {
            transparentStatusBar()
            navigationBarColor(R.color.navigationBarBackground)
            // 判断是否处于深色模式
            if (resources.getColor(R.color.colorPrimary, theme) == Color.parseColor("#78A2F5")) {
                this@BaseActivity.logger.info("用户正使用深色模式")
                navigationBarDarkIcon(false)
                statusBarDarkFont(false)
                darkMode = true
            } else {
                this@BaseActivity.logger.info("用户正使用浅色模式")
                navigationBarDarkIcon(true)
                statusBarDarkFont(true)
                darkMode = false
            }
            fitsSystemWindows(true)
        }
    }

    companion object {
        const val SHOW_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 0x005
    }
}