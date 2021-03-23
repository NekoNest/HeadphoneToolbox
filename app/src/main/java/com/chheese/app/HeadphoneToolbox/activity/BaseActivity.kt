package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.unit.dp
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel
import com.chheese.app.HeadphoneToolbox.ui.Color
import com.chheese.app.HeadphoneToolbox.ui.ShapeCornerSize
import com.chheese.app.HeadphoneToolbox.ui.ShapeType
import com.chheese.app.HeadphoneToolbox.ui.shape
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
                    when (shapeType.value!!) {
                        "round" -> ShapeType.ROUNDED
                        "cut" -> ShapeType.CUT
                        "none" -> ShapeType.NONE
                        else -> error("没有这种边角类型")
                    }, shapeCornerSize.value!!
                )
            }
        }
        SharedAppData.shapeType.observe(this) {
            viewModel.shapeType.value = when (it) {
                "round" -> ShapeType.ROUNDED
                "cut" -> ShapeType.CUT
                "none" -> ShapeType.NONE
                else -> error("没有这种边角类型")
            }
            SharedAppData.apply {
                shape.value = shape(
                    when (shapeType.value!!) {
                        "round" -> ShapeType.ROUNDED
                        "cut" -> ShapeType.CUT
                        "none" -> ShapeType.NONE
                        else -> error("没有这种边角类型")
                    }, shapeCornerSize.value!!
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

    companion object {
        const val SHOW_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 0x005
    }
}