package com.chheese.app.HeadphoneToolbox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.activity.LogListActivity
import com.chheese.app.HeadphoneToolbox.activity.PermissionManageActivity
import com.chheese.app.HeadphoneToolbox.activity.SplashActivity
import com.chheese.app.HeadphoneToolbox.activity.UiSettingsActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI

class SettingsFragment(private val inVisibleKeys: Array<String> = arrayOf()) :
    BaseFragment(R.xml.preference_settings) {
    private lateinit var playerSettings: PreferenceCategory
    private lateinit var selectPlayer: Preference
    private lateinit var viewLog: Preference
    private lateinit var openDetails: Preference
    private lateinit var openCoolapk: Preference
    private lateinit var about: Preference
    private lateinit var allowParallel: SwitchPreference
    private lateinit var useExperimentalFeature: SwitchPreference
    private lateinit var experimentalFeatures: PreferenceCategory
    private lateinit var useNewUi: SwitchPreference
    private lateinit var newUiSettings: Preference
    private lateinit var aboutAuthor: Preference
    private lateinit var checkForUpdates: Preference
    private lateinit var restartUi: Preference
    private lateinit var permissionManage: Preference

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

    override fun <T : Preference?> findPreference(key: CharSequence): T {
        return super.findPreference(key)!!
    }

    override fun initPreferences() {
        playerSettings = findPreference(PreferenceKeys.CATEGORY_PLAYER_SETTINGS)
        selectPlayer = findPreference(PreferenceKeys.PREF_SELECT_PLAYER)
        viewLog = findPreference(PreferenceKeys.PREF_VIEW_LOG)
        openDetails = findPreference(PreferenceKeys.PREF_OPEN_DETAILS)
        openCoolapk = findPreference(PreferenceKeys.PREF_OPEN_IN_COOLAPK)
        about = findPreference(PreferenceKeys.PREF_ABOUT)
        allowParallel = findPreference(PreferenceKeys.SWITCH_ALLOW_PARALLEL)
        useExperimentalFeature = findPreference(PreferenceKeys.SWITCH_USE_EXPERIMENTAL_FEATURE)
        experimentalFeatures = findPreference(PreferenceKeys.CATEGORY_EXPERIMENTAL_FEATURES)
        useNewUi = findPreference(PreferenceKeys.SWITCH_USE_NEW_UI)
        newUiSettings = findPreference(PreferenceKeys.PREF_NEW_UI_SETTINGS)
        aboutAuthor = findPreference(PreferenceKeys.PREF_ABOUT_AUTHOR)
        checkForUpdates = findPreference(PreferenceKeys.PREF_CHECK_FOR_UPDATES)
        restartUi = findPreference(PreferenceKeys.PREF_RESTART_UI)
        permissionManage = findPreference(PreferenceKeys.PREF_PERMISSION_MANAGE)

        playerSettings.isVisible = app.sharedPreferences
            .getBoolean(PreferenceKeys.SWITCH_OPEN_PLAYER, false)
        experimentalFeatures.isVisible = app.sharedPreferences
            .getBoolean(PreferenceKeys.SWITCH_USE_EXPERIMENTAL_FEATURE, false)

        selectPlayer.setOnPreferenceClickListener(this::onSelectPlayerClick)
        val selectedPackage = app.sharedPreferences.get(PreferenceKeys.PREF_SELECT_PLAYER, "")
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
            goToCoolapk()
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

        useExperimentalFeature.setOnPreferenceClickListener {
            val isChecked = (it as SwitchPreference).isChecked
            experimentalFeatures.isVisible = isChecked
            if (isChecked) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("二次确认")
                    .setMessage("实验版特性一般处于开发阶段，可能存在大量bug，甚至导致应用崩溃，确定要开启吗？")
                    .setCancelable(false)
                    .setPositiveButton("确定") { _, _ -> }
                    .setNegativeButton("还没") { _, _ ->
                        useExperimentalFeature.isChecked = false
                        experimentalFeatures.isVisible = false
                        useNewUi.isChecked = false
                    }.create().show()
            } else {
                makeRestartAppSnackbar()
                useNewUi.isChecked = false
            }
            true
        }

        useNewUi.setOnPreferenceClickListener {
            (it as SwitchPreference).apply {
                newUiSettings.isVisible = isChecked
            }
            makeRestartAppSnackbar()
            true
        }

        newUiSettings.isVisible = useNewUi.isChecked
        newUiSettings.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), UiSettingsActivity::class.java))
            true
        }

        aboutAuthor.setOnPreferenceClickListener(this::onAboutAuthorClick)

        checkForUpdates.setOnPreferenceClickListener(this::checkForUpdates)

        restartUi.setOnPreferenceClickListener {
            restartUi()
            true
        }

        permissionManage.setOnPreferenceClickListener {
            startActivity(Intent(requireContext(), PermissionManageActivity::class.java))
            true
        }

        for (key in inVisibleKeys) {
            findPreference<Preference>(key).isVisible = false
        }
    }

    private fun goToCoolapk() {
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
    }

    @Throws(IOException::class, MalformedURLException::class)
    private fun checkForUpdates(pref: Preference): Boolean {
        val snackbar = Snackbar.make(requireView(), "请稍等", 5000)
        snackbar.show()
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val _this = this@SettingsFragment
                _this.logger.info("开始检查更新")
                val url = runCatching {
                    URI.create("https://www.coolapk.com/apk/com.chheese.app.HeadphoneToolbox")
                        .toURL()
                }.getOrNull() ?: return@withContext

                val doc = runCatching {
                    Jsoup.parse(url, 5000)
                }.getOrNull() ?: return@withContext
                _this.logger.info("已取到酷安APP详情页面Document")

                val newVersionName = doc.select("span.list_app_info").text()
                val versionName =
                    requireActivity().packageManager.getPackageInfo(app.packageName, 0).versionName
                if (newVersionName != versionName) {
                    _this.logger.info("发现更新，版本名称：$newVersionName")
                    snackbar.dismiss()
                    val newVersionInfoNodes = doc.select(".apk_left_title_info")[1].textNodes()
                    var newVersionInfo = ""
                    newVersionInfoNodes.forEach {
                        newVersionInfo += it.wholeText.trim() + "\n"
                    }
                    requireActivity().runOnUiThread {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("发现更新 $newVersionName")
                            .setMessage("新版特性\n$newVersionInfo")
                            .setPositiveButton("前往酷安") { i, which ->
                                _this.goToCoolapk()
                            }
                            .create()
                            .show()
                    }
                } else {
                    _this.logger.info("没有发现更新")
                    Snackbar.make(requireView(), "没有发现更新", 3000).show()
                }
            }
        }
        return true
    }

    private fun restartUi(v: View? = null) {
        val intent = Intent(requireContext(), SplashActivity::class.java)
        // CLEAR_TASK 必须和 NEW_TASK 同时使用才可起到清空Activity栈的效果
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun makeRestartAppSnackbar() {
        Snackbar.make(requireContext(), requireView(), "关闭并重启用户界面后生效", 3000)
            .setAction("立即重启", this::restartUi)
            .show()
    }

    override fun addObservers() {
        if (inVisibleKeys.isNotEmpty()) return
        SharedAppData.openPlayer.observe(this) {
            playerSettings.isVisible = it
        }
    }

    override fun onBatteryPermissionGrantFailed() {

    }

    private fun onAboutClick(pref: Preference): Boolean {
        AlertDialog.Builder(requireActivity())
            .setTitle("关于此应用")
            .setMessage(
                """
                耳机工具箱是一个耳机相关工具的集合
                可加入QQ群或在评论区进行意见反馈
                官方QQ群：594824871""".trimIndent()
            )
            .setPositiveButton("了解") { _, _ -> }
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

    @SuppressLint("QueryPermissionsNeeded")
    private fun onSelectPlayerClick(pref: Preference): Boolean {
        val pm = requireContext().packageManager

        val labelPackageMap = HashMap<String, String>()
        labelPackageMap["不选择"] = ""
        val labels = ArrayList<String>()
        labels.add("不选择")

        pm.getInstalledApplications(0).filter {
            if (app.sharedPreferences.get(PreferenceKeys.SWITCH_SHOW_ALL_APPS, false)) {
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
}