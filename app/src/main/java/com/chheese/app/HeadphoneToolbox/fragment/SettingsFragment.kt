package com.chheese.app.HeadphoneToolbox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.activity.LogListActivity
import com.chheese.app.HeadphoneToolbox.edit
import com.chheese.app.HeadphoneToolbox.get
import com.google.android.material.textfield.TextInputEditText

class SettingsFragment : AbstractPreferenceFragment(R.xml.preference_settings) {
    @BindKey("background_method")
    private lateinit var backgroundMethod: Preference

    @BindKey("custom_notify")
    private lateinit var customNotify: Preference

    @BindKey("player_settings")
    private lateinit var playerSettings: PreferenceCategory

    @BindKey("alert_on_open")
    private lateinit var alertBeforeOpen: SwitchPreference

    @BindKey("select_player")
    private lateinit var selectPlayer: Preference

    @BindKey("view_log")
    private lateinit var viewLog: Preference

    @BindKey("open_details")
    private lateinit var openDetails: Preference

    @BindKey("open_in_coolapk")
    private lateinit var openCoolapk: Preference

    @BindKey("about")
    private lateinit var about: Preference

    @BindKey("allow_pararell")
    private lateinit var allowParallel: SwitchPreference

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

    override fun init() {
        backgroundMethod.setOnPreferenceClickListener(this::onBackgroundMethodClick)
        val selectedBackgroundMethod =
            app.sharedPreferences.getString(res.getString(R.string.backgroundMethod), "")
        backgroundMethod.summary = if (selectedBackgroundMethod == "") {
            "当前没有选择"
        } else {
            "当前已选择：$selectedBackgroundMethod"
        }
        playerSettings.isVisible = app.sharedPreferences
            .getBoolean(res.getString(R.string.openPlayer), false)
        customNotify.setOnPreferenceClickListener(this::onCustomNotifyClick)
        customNotify.isVisible =
            selectedBackgroundMethod == res.getStringArray(R.array.backgroundMethods)[0]

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            backgroundMethod.isVisible = false
            customNotify.isVisible = false
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
    }

    private fun onAboutClick(pref: Preference): Boolean {
        AlertDialog.Builder(requireActivity())
            .setTitle("关于")
            .setMessage(
                """
                作者：
                LemonNeko柠喵
                感谢：
                可爱的酷友们 和 LiliumNeko百合喵""".trimIndent()
            )
            .setPositiveButton("知道啦") { _, _ -> }
            .setNegativeButton("请柠喵喝可乐") { _, _ ->
                val uri = Uri.parse("https://afdian.net/@TAGP0")
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = uri
                startActivity(intent)
            }.setNeutralButton("去柠喵的B站空间") { _, _ ->
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
            val label = it.loadLabel(pm)
            val packageName = it.packageName
            label.contains("music", true)
                    || label.contains("音乐")
                    || packageName.contains("music", true)
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

    private fun onBackgroundMethodClick(pref: Preference): Boolean {
        val itemArray = res.getStringArray(R.array.backgroundMethods)
        val checkedItem =
            itemArray.indexOf(app.sharedPreferences.get(res, R.string.backgroundMethod, ""))
        AlertDialog.Builder(requireContext())
            .setSingleChoiceItems(itemArray, checkedItem) { dialog, which ->
                val selected = res.getStringArray(R.array.backgroundMethods)[which]
                app.sharedPreferences.edit {
                    putString(pref.key, selected)
                }
                pref.summary = "当前已选择：$selected"
                if (which == 0) {
                    customNotify.isVisible = true
                } else if (which == 1) {
                    requestIgnoreBatteryOptimizations()
                    customNotify.isVisible = false
                }
                dialog.cancel()
            }.create().show()
        return true
    }

    @SuppressLint("InflateParams")
    private fun onCustomNotifyClick(pref: Preference): Boolean {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_cutom_notify, null)
        val titleEditor = dialogView.findViewById<TextInputEditText>(R.id.title_edit)
        val messageEditor = dialogView.findViewById<TextInputEditText>(R.id.message_edit)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("完成") { _, _ ->
                app.sharedPreferences.edit {
                    val title = titleEditor.text
                    if (title != null) {
                        putString("custom_notify_title", title.toString())
                    }
                    val message = messageEditor.text
                    if (message != null) {
                        putString("custom_notify_message", message.toString())
                    }
                }
            }.setNegativeButton("取消") { _, _ -> }
            .create().show()
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
                    backgroundMethod.summary = "当前没有选择"
                    app.sharedPreferences.edit {
                        putString(backgroundMethod.key, "")
                    }
                }
                .create().show()
        }
    }

    companion object {
        const val FLAG_IGNORE_BATTERY_OPTIMIZATIONS = 0xf001
        const val REQUEST_ALERT_PERMISSION = 0xf05
    }
}