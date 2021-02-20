package com.chheese.app.HeadphoneToolbox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Message
import android.view.LayoutInflater
import androidx.annotation.RawRes
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.activity.ToolboxActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.util.logger
import com.chheese.app.HeadphoneToolbox.util.newMessage
import com.chheese.app.HeadphoneToolbox.util.setTo
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class MainFragment : BaseFragment(R.xml.preference_main) {
    private var leftAudioMap: Map<Int, Int>? = null
    private var rightAudioMap: Map<Int, Int>? = null

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

    @RawRes
    private fun randAudio(right: Boolean = false): Int {
        val audioMap: Map<Int, Int>
        if (!right) {
            if (leftAudioMap == null) {
                leftAudioMap = mapOf(
                    0 to R.raw.are_you_ok_left,
                    1 to R.raw.deep_dark_fantasy_left,
                    2 to R.raw.duang_left,
                    3 to R.raw.niconiconi_left
                )
            }
            audioMap = leftAudioMap!!
        } else {
            if (rightAudioMap == null) {
                rightAudioMap = mapOf(
                    0 to R.raw.are_you_ok_right,
                    1 to R.raw.deep_dark_fantasy_right,
                    2 to R.raw.duang_right,
                    3 to R.raw.niconiconi_right
                )
            }
            audioMap = rightAudioMap!!
        }

        val randInt = Random.nextInt(0, 4)
        return audioMap[randInt] ?: error("Index $randInt is not in map.")
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