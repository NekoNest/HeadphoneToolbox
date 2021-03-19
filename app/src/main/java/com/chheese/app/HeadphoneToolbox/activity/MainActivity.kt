package com.chheese.app.HeadphoneToolbox.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel
import com.chheese.app.HeadphoneToolbox.fragment.SettingsFragment
import com.chheese.app.HeadphoneToolbox.ui.Home
import com.chheese.app.HeadphoneToolbox.ui.theme.ToolboxTheme
import com.chheese.app.HeadphoneToolbox.util.logger
import com.chheese.app.HeadphoneToolbox.util.randAudio
import com.chheese.app.HeadphoneToolbox.util.setTo
import com.google.android.material.button.MaterialButton

class MainActivity : ToolboxBaseActivity() {
    private val viewModel: ToolboxViewModel by viewModels()

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedAppData.openPlayer.observe(this) {
            viewModel.openPlayer.value = it
        }

        SharedAppData.lightScreen.observe(this) {
            viewModel.lightScreen.value = it
        }

        if (darkMode) {
            viewModel.theme.value = ToolboxTheme.dark
        } else {
            viewModel.theme.value = ToolboxTheme.light
        }

        setContent {
            ToolboxTheme(
                viewModel = viewModel,
                content = {
                    Home(
                        viewModel = viewModel,
                        mainActivity = this
                    )
                }
            )
        }
    }

    override fun onBatteryPermissionGrantFailed() {
        SharedAppData.openPlayer setTo false
        SharedAppData.lightScreen setTo false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SettingsFragment.FLAG_IGNORE_BATTERY_OPTIMIZATIONS) {
            when (resultCode) {
                0 -> {
                    logger.info("用户拒绝了请求")
                    onIgnoreBatteryOptimizationActivityReject()
                }
                -1 -> {
                    logger.info("用户同意了请求")
                }
            }
        }
    }

    internal fun showChannelTestDialog() {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_channel_test, null)
        val leftButton = dialogView.findViewById<MaterialButton>(R.id.channel_test_left)
        val rightButton = dialogView.findViewById<MaterialButton>(R.id.channel_test_right)

        leftButton.setOnClickListener {
            playMedia(randAudio())
        }
        rightButton.setOnClickListener {
            playMedia(randAudio(true))
        }
        AlertDialog.Builder(this)
            .setMessage("点击按钮测试相应声道，点击对话框外部退出")
            .setView(dialogView)
            .create().show()
    }
}