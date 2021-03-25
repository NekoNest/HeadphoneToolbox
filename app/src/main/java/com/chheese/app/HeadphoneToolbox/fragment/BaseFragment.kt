package com.chheese.app.HeadphoneToolbox.fragment

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.activity.ToolboxBaseActivity
import com.chheese.app.HeadphoneToolbox.util.isIgnoringBatteryOptimizations
import com.chheese.app.HeadphoneToolbox.util.logger

abstract class BaseFragment(@XmlRes private val resId: Int) :
    PreferenceFragmentCompat() {
    protected lateinit var app: HeadphoneToolbox
    protected lateinit var res: Resources
    protected var messageCallback: ((Int) -> Unit)? = null

    internal var handler = Handler(Looper.getMainLooper(), this::handleMessage)

    protected open fun handleMessage(message: Message): Boolean {
        if (message.what == REQUEST_IGNORE_BATTERY_OPTIMIZATION_FAILED) {
            onBatteryPermissionGrantFailed()
        }
        return true
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            0 -> {
                logger.info("用户拒绝了请求")
                (requireActivity() as ToolboxBaseActivity).onIgnoreBatteryOptimizationActivityReject()
            }
            -1 -> {
                logger.info("用户同意了请求")
            }
        }
    }

    protected abstract fun onBatteryPermissionGrantFailed()

    companion object {
        const val FLAG_FRAGMENT_SHOWN = 0xf002
        const val REQUEST_IGNORE_BATTERY_OPTIMIZATION_FAILED = 0x003
    }
}