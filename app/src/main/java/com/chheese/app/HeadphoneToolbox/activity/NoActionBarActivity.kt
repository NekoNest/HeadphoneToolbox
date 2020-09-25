package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar

open class NoActionBarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            transparentStatusBar()
            transparentNavigationBar()
            navigationBarColor(android.R.color.white)
            autoNavigationBarDarkModeEnable(true)
            statusBarDarkFont(true)
            fitsSystemWindows(true, android.R.color.white)
        }
    }
}