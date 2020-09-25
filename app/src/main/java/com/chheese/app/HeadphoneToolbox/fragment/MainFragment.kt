package com.chheese.app.HeadphoneToolbox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RawRes
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.activity.ToolboxActivity
import com.chheese.app.HeadphoneToolbox.get
import com.chheese.app.HeadphoneToolbox.newMessage
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class MainFragment : AbstractPreferenceFragment(R.xml.preference_main) {
    private var leftAudioMap: Map<Int, Int>? = null
    private var rightAudioMap: Map<Int, Int>? = null

    @BindKey(R.string.lightScreen)
    private lateinit var lightScreen: SwitchPreference

    @BindKey(R.string.openPlayer)
    private lateinit var openPlayer: SwitchPreference

    @BindKey(R.string.channelTest)
    private lateinit var channelTest: Preference

    override fun init() {
        lightScreen.setOnPreferenceClickListener(this::checkBackgroundMethodSetting)
        openPlayer.setOnPreferenceClickListener(this::checkBackgroundMethodSetting)
        channelTest.setOnPreferenceClickListener(this::onChannelTestClick)
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

    private fun checkBackgroundMethodSetting(preference: Preference): Boolean {
        val pref = preference as SwitchPreference
        val value = preference.isChecked
        val switchName = when (preference.key) {
            res.getString(R.string.lightScreen) -> "点亮屏幕"
            res.getString(R.string.openPlayer) -> "打开播放器"
            else -> ""
        }
        app.logger.info("${switchName}开关状态被改变，新状态：$value")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val backgroundMethod =
                app.sharedPreferences.get(app.resources, R.string.backgroundMethod, "")
            if (value && (backgroundMethod == "")) {
                newBackgroundMethodDialog(requireActivity() as ToolboxActivity, pref).show()
                return false
            }
        } else {
            requestIgnoreBatteryOptimizations()
        }
        return true
    }

    override fun onIgnoreBatteryOptimizationActivity(accept: Boolean) {
        if (!accept) {
            AlertDialog.Builder(requireContext())
                .setTitle("诶？")
                .setMessage("你拒绝了权限请求，是手滑了吗？")
                .setPositiveButton("是，再来一次") { _, _ ->
                    requestIgnoreBatteryOptimizations()
                }.setNegativeButton("没有，我反悔了") { _, _ ->
                    lightScreen.isChecked = false
                    openPlayer.isChecked = false
                }
                .create().show()
        }
    }
}