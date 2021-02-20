package com.chheese.app.HeadphoneToolbox.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.util.isIgnoringBatteryOptimizations
import com.chheese.app.HeadphoneToolbox.util.logger

abstract class BaseFragment(@XmlRes private val resId: Int) :
    PreferenceFragmentCompat() {
    protected lateinit var app: HeadphoneToolbox
    protected lateinit var res: Resources
    protected var messageCallback: ((Int) -> Unit)? = null

    var handler = Handler(Looper.getMainLooper(), this::handleMessage)

    protected abstract fun handleMessage(message: Message): Boolean

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        app = requireActivity().application as HeadphoneToolbox
        res = requireActivity().resources

        addPreferencesFromResource(resId)

        initPreferences()
        addObservers()
    }

    /**
     * 子类将在这里初始化Preference
     */
    protected abstract fun initPreferences()

    /**
     * 子类将在这里注册数据观察者
     */
    protected abstract fun addObservers()

    /**
     * 检查电池优化权限
     */
    protected fun isIgnoringBatteryOptimizations() =
        requireActivity().isIgnoringBatteryOptimizations()

    protected fun requestBatteryPermission() {
        logger.info("正在请求忽略电池优化权限")
        requestIgnoreBatteryOptimizations()
    }

    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected fun requestIgnoreBatteryOptimizations() {
        val pm = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnored = pm.isIgnoringBatteryOptimizations(app.packageName)
        if (isIgnored) return
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.fromParts("package", app.packageName, null)
        startActivityForResult(intent, SettingsFragment.FLAG_IGNORE_BATTERY_OPTIMIZATIONS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SettingsFragment.FLAG_IGNORE_BATTERY_OPTIMIZATIONS) {
            when (resultCode) {
                0 -> {
                    logger.info("用户拒绝了请求")
                    onIgnoreBatteryOptimizationActivity(false)
                }
                -1 -> {
                    logger.info("用户同意了请求")
                    onIgnoreBatteryOptimizationActivity(true)
                }
            }
        }
    }

    private fun onIgnoreBatteryOptimizationActivity(accept: Boolean) {
        if (!accept) {
            AlertDialog.Builder(requireContext())
                .setTitle("诶？")
                .setMessage("你拒绝了权限请求，是手滑了吗？")
                .setPositiveButton("是，再来一次") { _, _ ->
                    requestIgnoreBatteryOptimizations()
                }.setNegativeButton("没有，我反悔了") { _, _ ->
                    onBatteryPermissionGrantFailed()
                }
                .create().show()
        }
    }

    protected abstract fun onBatteryPermissionGrantFailed()

    companion object {
        const val FLAG_FRAGMENT_SHOWN = 0xf002
        const val REQUEST_IGNORE_BATTERY_OPTIMIZATION_FAILED = 0x003
    }
}