package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.gyf.immersionbar.ktx.immersionBar

open class NoActionBarActivity : AppCompatActivity() {
    protected lateinit var app: HeadphoneToolbox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as HeadphoneToolbox
        immersionBar {
            transparentStatusBar()
            transparentNavigationBar()
            autoNavigationBarDarkModeEnable(true)
            statusBarDarkFont(true)
            fitsSystemWindows(true)
        }
    }
}