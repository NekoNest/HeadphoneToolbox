package com.chheese.app.HeadphoneToolbox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Message
import android.view.LayoutInflater
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.activity.ToolboxActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.util.*
import com.google.android.material.button.MaterialButton

class MainFragment : BaseFragment(R.xml.preference_main) {
    private lateinit var lightScreen: SwitchPreference
    private lateinit var openPlayer: SwitchPreference
    private lateinit var channelTest: Preference

    override fun initPreferences() {
        lightScreen = findPreference("light_screen")!!
        openPlayer = findPreference("open_player")!!
        channelTest = findPreference("channel_test")!!

        lightScreen.setOnPreferenceClickListener(this::onLightScreenPrefClick)
        openPlayer.setOnPreferenceClickListener(this::onOpenPlayerPrefClick)
        channelTest.setOnPreferenceClickListener(this::onChannelTestClick)
    }

    override fun addObservers() {
        SharedAppData.lightScreen.observe(this) {
            lightScreen.isChecked = it
            lightScreen.icon = if (it) {
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_light_screen_on,
                    requireActivity().theme
                )
            } else {
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_light_screen_off,
                    requireActivity().theme
                )
            }
        }
        SharedAppData.openPlayer.observe(this) {
            openPlayer.isChecked = it
        }
    }

    /**
     * 当功能【点亮屏幕】开关被点击时触发的操作
     */
    private fun onLightScreenPrefClick(pref: Preference): Boolean {
        (pref as SwitchPreference).apply {
            // 关掉开关就不用管了吧
            SharedAppData.lightScreen.value = isChecked
            if (!isChecked) {
                logger.info("用户在应用内关闭了功能【点亮屏幕】的开关")
                return@apply
            }
            logger.info("用户在应用内打开了功能【点亮屏幕】的开关")
            // 检查权限
            if (!isIgnoringBatteryOptimizations()) {
                logger.info("需要忽略电池优化权限")
                requestBatteryPermission()
            }
        }
        return true
    }

    private fun onOpenPlayerPrefClick(pref: Preference): Boolean {
        (pref as SwitchPreference).apply {
            // 关掉开关就不用管了吧
            SharedAppData.openPlayer.value = isChecked
            if (!isChecked) {
                logger.info("用户在应用内关闭了功能【打开播放器】的开关")
                return@apply
            }
            logger.info("用户在应用内打开了功能【打开播放器】的开关")
            // 检查权限
            if (!isIgnoringBatteryOptimizations()) {
                logger.info("需要忽略电池优化权限")
                requestBatteryPermission()
            }
        }
        return true
    }

    @SuppressLint("InflateParams")
    private fun onChannelTestClick(pref: Preference): Boolean {
        val dialogView = LayoutInflater.from(requireActivity())
            .inflate(R.layout.dialog_channel_test, null)
        val leftButton = dialogView.findViewById<MaterialButton>(R.id.channel_test_left)
        val rightButton = dialogView.findViewById<MaterialButton>(R.id.channel_test_right)
        val host = requireActivity() as ToolboxActivity
        leftButton.setOnClickListener {
            host.handler.sendMessage(newMessage(ToolboxActivity.FLAG_PLAY_AUDIO, randAudio()))
        }
        rightButton.setOnClickListener {
            host.handler.sendMessage(newMessage(ToolboxActivity.FLAG_PLAY_AUDIO, randAudio(true)))
        }
        AlertDialog.Builder(requireActivity())
            .setMessage("点击按钮测试相应声道，点击对话框外部退出")
            .setView(dialogView)
            .create().show()
        return true
    }

    override fun onBatteryPermissionGrantFailed() {
        lightScreen.isChecked = false
        openPlayer.isChecked = false
        SharedAppData.lightScreen setTo false
        SharedAppData.openPlayer setTo false
    }

    override fun handleMessage(message: Message): Boolean {
        if (message.what == REQUEST_IGNORE_BATTERY_OPTIMIZATION_FAILED) {
            lightScreen.isChecked = false
            openPlayer.isChecked = false
            SharedAppData.lightScreen setTo false
            SharedAppData.openPlayer setTo false
        }
        return true
    }
}