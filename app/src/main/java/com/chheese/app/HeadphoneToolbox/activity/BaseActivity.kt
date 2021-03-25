package com.chheese.app.HeadphoneToolbox.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.unit.dp
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel
import com.chheese.app.HeadphoneToolbox.ui.components.*
import com.chheese.app.HeadphoneToolbox.ui.pages.Color
import com.chheese.app.HeadphoneToolbox.util.*
import com.gyf.immersionbar.ktx.immersionBar

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var app: HeadphoneToolbox
    protected val viewModel: ToolboxViewModel by viewModels()
    protected var darkMode = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = application as HeadphoneToolbox
        immersionBar {
            transparentStatusBar()
            navigationBarColor(R.color.navigationBarBackground)
            // 判断是否处于深色模式
            if (resources.getColor(R.color.colorPrimary, theme) == 0x78A2F5) {
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

        SharedAppData.topStartCornerSize.observe(this) {
            viewModel.topStartCornerSize.value = it
            SharedAppData.apply {
                shapeCornerSize.value = ShapeCornerSize(
                    topStartCornerSize.value!!.dp,
                    topEndCornerSize.value!!.dp,
                    bottomStartCornerSize.value!!.dp,
                    bottomEndCornerSize.value!!.dp
                )
            }
        }

        SharedAppData.topEndCornerSize.observe(this) {
            viewModel.topEndCornerSize.value = it
            SharedAppData.apply {
                shapeCornerSize.value = ShapeCornerSize(
                    topStartCornerSize.value!!.dp,
                    topEndCornerSize.value!!.dp,
                    bottomStartCornerSize.value!!.dp,
                    bottomEndCornerSize.value!!.dp
                )
            }
        }
        SharedAppData.bottomStartCornerSize.observe(this) {
            viewModel.bottomStartCornerSize.value = it
            SharedAppData.apply {
                shapeCornerSize.value = ShapeCornerSize(
                    topStartCornerSize.value!!.dp,
                    topEndCornerSize.value!!.dp,
                    bottomStartCornerSize.value!!.dp,
                    bottomEndCornerSize.value!!.dp
                )
            }
        }
        SharedAppData.bottomEndCornerSize.observe(this) {
            viewModel.bottomEndCornerSize.value = it
            SharedAppData.apply {
                shapeCornerSize.value = ShapeCornerSize(
                    topStartCornerSize.value!!.dp,
                    topEndCornerSize.value!!.dp,
                    bottomStartCornerSize.value!!.dp,
                    bottomEndCornerSize.value!!.dp
                )
            }
        }
        SharedAppData.shapeCornerSize.observe(this) {
            viewModel.shapeCornerSize.value = it
            SharedAppData.apply {
                shape.value = shape(
                    ShapeType.valueTo(shapeType.value!!), shapeCornerSize.value!!
                )
            }
        }
        SharedAppData.shapeType.observe(this) {
            viewModel.shapeType.value = ShapeType.valueTo(it)
            SharedAppData.apply {
                shape.value = shape(
                    ShapeType.valueTo(it), shapeCornerSize.value!!
                )
            }
        }
        SharedAppData.shape.observe(this) {
            viewModel.shape.value = it
        }

        SharedAppData.colorPrimary.observe(this) {
            val isLight = viewModel.theme.value.isLight
            if (isLight) {
                viewModel.theme.value = lightColors(
                    primary = Color(it),
                    secondary = Color(it)
                )
            } else {
                viewModel.theme.value = darkColors(
                    primary = Color(it),
                    secondary = Color(it)
                )
            }
            viewModel.colorTextFieldValue.value = "#$it"
        }
    }

    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    internal fun requestIgnoreBatteryOptimizations() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnored = pm.isIgnoringBatteryOptimizations(app.packageName)
        if (isIgnored) {
            logger.warn("已经获得授权时不应该调用此方法")
            return
        }
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.fromParts("package", app.packageName, null)
        startActivityForResult(intent, 0)
    }

    companion object {
        const val SHOW_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 0x005
    }
}