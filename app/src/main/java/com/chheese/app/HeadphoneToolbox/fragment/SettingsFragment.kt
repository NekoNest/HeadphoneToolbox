package com.chheese.app.HeadphoneToolbox.fragment

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.activity.AdScreen
import com.chheese.app.HeadphoneToolbox.activity.LogListActivity
import com.chheese.app.HeadphoneToolbox.util.edit
import com.chheese.app.HeadphoneToolbox.util.get

class SettingsFragment : BaseFragment(R.xml.preference_settings) {
    private lateinit var playerSettings: PreferenceCategory
    private lateinit var alertBeforeOpen: SwitchPreference
    private lateinit var selectPlayer: Preference
    private lateinit var viewLog: Preference
    private lateinit var openDetails: Preference
    private lateinit var openCoolapk: Preference
    private lateinit var about: Preference
    private lateinit var allowParallel: SwitchPreference
    private lateinit var enableExperimentalFeature: SwitchPreference
    private lateinit var aboutAuthor: Preference
    private lateinit var adScreen: Preference

    init {
        messageCallback = {
            if (it == FLAG_FRAGMENT_SHOWN) {
                if (this::playerSettings.isInitialized) {
                    playerSettings.isVisible = app.sharedPreferences
                        .getBoolean(res.getString(R.string.openPlayer), false)
                }
            }
        }
    }

    override fun initPreferences() {
        playerSettings = findPreference("player_settings")!!
        alertBeforeOpen = findPreference("alert_on_open")!!
        selectPlayer = findPreference("select_player")!!
        viewLog = findPreference("view_log")!!
        openDetails = findPreference("open_details")!!
        openCoolapk = findPreference("open_in_coolapk")!!
        about = findPreference("about")!!
        allowParallel = findPreference("allow_parallel")!!
        enableExperimentalFeature = findPreference("enable_experimental_feature")!!
        aboutAuthor = findPreference("about_author")!!
        adScreen = findPreference("ad_screen")!!

        playerSettings.isVisible = app.sharedPreferences
            .getBoolean(res.getString(R.string.openPlayer), false)
        adScreen.isVisible =
            app.sharedPreferences.get(res, R.string.enableExperimentalFeature, false)

        adScreen.setOnPreferenceClickListener {
            startActivity(Intent(requireActivity(), AdScreen::class.java))
            true
        }

        alertBeforeOpen.setOnPreferenceClickListener(this::onAlertOnOpenPrefClick)
        selectPlayer.setOnPreferenceClickListener(this::onSelectPlayerClick)
        val selectedPackage = app.sharedPreferences.get(res, R.string.selectPlayer, "")
        val matchedApp = app.packageManager.getInstalledApplications(0).filter {
            it.packageName == selectedPackage
        }

        if (matchedApp.isEmpty()) {
            selectPlayer.summary = "当前没有选择播放器"
        } else {
            selectPlayer.summary = "当前已选择${matchedApp[0].loadLabel(app.packageManager)}"
        }

        viewLog.setOnPreferenceClickListener {
            startActivity(Intent(requireActivity(), LogListActivity::class.java))
            true
        }

        about.setOnPreferenceClickListener(this::onAboutClick)
        val versionName =
            requireActivity().packageManager.getPackageInfo(app.packageName, 0).versionName
        about.summary = "版本：$versionName"

        openDetails.setOnPreferenceClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", app.packageName, null)
            startActivity(intent)
            true
        }
        openCoolapk.setOnPreferenceClickListener {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.chheese.app.HeadphoneToolbox")
                )
                intent.setPackage("com.coolapk.market")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(app, "并没有安装酷安", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.coolapk.com/apk/com.chheese.app.HeadphoneToolbox")
                )
                startActivity(intent)
            }
            true
        }
        allowParallel.setOnPreferenceClickListener {
            if (allowParallel.isChecked) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("等等")
                    .setMessage("大量重复播放可能导致内存泄漏，是否继续？")
                    .setPositiveButton("是") { _, _ -> }
                    .setNegativeButton("算了") { _, _ ->
                        allowParallel.isChecked = false
                    }.create().show()
            }
            true
        }

        enableExperimentalFeature.setOnPreferenceClickListener {
            if (enableExperimentalFeature.isChecked) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("二次确认")
                    .setMessage("实验版特性一般处于开发阶段，可能存在大量bug，甚至导致应用崩溃，是否继续开启？打开或关闭后应用需要重启。")
                    .setCancelable(false)
                    .setPositiveButton("我爱做实验！") { _, _ ->
                        requireActivity().getSystemService<ActivityManager>()
                            ?.killBackgroundProcesses(requireActivity().packageName)
                    }
                    .setNegativeButton("拜拜了您嘞") { _, _ ->
                        enableExperimentalFeature.isChecked = false
                    }.create().show()
            } else {
                val activityManager = requireActivity().getSystemService<ActivityManager>()
                activityManager?.killBackgroundProcesses(requireActivity().packageName)
            }
            true
        }

        aboutAuthor.setOnPreferenceClickListener(this::onAboutAuthorClick)
    }

    override fun addObservers() {

    }

    override fun onBatteryPermissionGrantFailed() {

    }

    private fun onAboutClick(pref: Preference): Boolean {
        AlertDialog.Builder(requireActivity())
            .setTitle("关于此应用")
            .setMessage(
                """
                耳机工具箱是一个耳机相关工具的集合
                意见反馈可加入QQ群
                官方QQ群：594824871""".trimIndent()
            )
            .setPositiveButton("知道啦") { _, _ -> }
            .setNegativeButton("查看Github仓库") { _, _ ->
                val uri = Uri.parse("https://github.com/NekoNest/HeadphoneToolbox")
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri
                startActivity(intent)
            }
            .create().show()
        return true
    }

    private fun onAboutAuthorClick(pref: Preference): Boolean {
        AlertDialog.Builder(requireActivity())
            .setTitle("关于柠喵")
            .setMessage("柠喵是一只跨性别猫猫，一名贫穷的独立开发者，参与ArcLight开发，主导耳机工具箱开发，如果想支持猫猫可以到独立广告屏里点一下广告")
            .setPositiveButton("知道啦") { _, _ -> }
            .setNeutralButton("去柠喵的B站空间") { _, _ ->
                val uri = Uri.parse("https://space.bilibili.com/5325421")
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri
                startActivity(intent)
            }
            .create().show()
        return true
    }

    private fun onAlertOnOpenPrefClick(preference: Preference): Boolean {
        val pref = preference as SwitchPreference
        if (pref.isChecked && !Settings.canDrawOverlays(requireContext())) {
            AlertDialog.Builder(requireActivity())
                .setTitle("《关于权限请求的说明》")
                .setMessage("为了能在后台弹出\"是否打开播放器\"的对话框，我们应用需要获取一个称为\"允许调用系统级对话框\"的权限，请求批准")
                .setPositiveButton("批准请求") { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data = Uri.fromParts("package", requireContext().packageName, null)
                    startActivityForResult(intent, REQUEST_ALERT_PERMISSION)
                }.setNegativeButton("驳回请求") { _, _ ->
                    pref.isChecked = false
                }.create().show()
        }
        return true
    }

    private fun onSelectPlayerClick(pref: Preference): Boolean {
        val pm = requireContext().packageManager

        val labelPackageMap = HashMap<String, String>()
        labelPackageMap["不选择"] = ""
        val labels = ArrayList<String>()
        labels.add("不选择")

        pm.getInstalledApplications(0).filter {
            if (app.sharedPreferences.get(res, R.string.showAllApps, false)) {
                pm.getLaunchIntentForPackage(it.packageName) != null
            } else {
                val label = it.loadLabel(pm)
                val packageName = it.packageName
                (label.contains("music", true)
                        || label.contains("音乐")
                        || packageName.contains("music", true))
                        && pm.getLaunchIntentForPackage(it.packageName) != null
            }
        }.forEach {
            val label = it.loadLabel(pm).toString()
            labels.add(label)
            labelPackageMap[label] = it.packageName
        }

        val labelArray = labels.toTypedArray()
        AlertDialog.Builder(requireActivity())
            .setTitle("选择一个播放器吧！")
            .setSingleChoiceItems(labelArray, -1) { dialog, which ->
                app.sharedPreferences.edit {
                    putString(pref.key, labelPackageMap[labelArray[which]])
                }
                if (which == 0) {
                    pref.summary = "当前没有选择播放器"
                } else {
                    pref.summary = "当前已选择${labelArray[which]}"
                }
                dialog.cancel()
            }.create().show()
        return true
    }

    override fun handleMessage(message: Message) = true

    companion object {
        const val FLAG_IGNORE_BATTERY_OPTIMIZATIONS = 0xf001
        const val REQUEST_ALERT_PERMISSION = 0xf05
    }
}