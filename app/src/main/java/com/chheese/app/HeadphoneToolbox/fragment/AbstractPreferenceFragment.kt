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
import androidx.annotation.StringRes
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.activity.ToolboxActivity
import com.chheese.app.HeadphoneToolbox.util.*
import java.lang.reflect.Field

abstract class AbstractPreferenceFragment(@XmlRes private val resId: Int) :
    PreferenceFragmentCompat() {
    protected lateinit var app: HeadphoneToolbox
    protected lateinit var res: Resources
    protected var messageCallback: ((Int) -> Unit)? = null

    var handler = Handler(Looper.getMainLooper()) {
        if (messageCallback == null) {
            return@Handler false
        }
        messageCallback!!(it.what)
        true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        app = requireActivity().application as HeadphoneToolbox
        res = requireActivity().resources

        addPreferencesFromResource(resId)
        logger.info("开始获取Fragment内所有Preference")
        val clazz = this.javaClass
        val fieldKeyMap = HashMap<Field, String>()
        val fields = clazz.declaredFields.filter {
            val annotation = it.getAnnotation(BindKey::class.java)
            if (annotation != null) {
                fieldKeyMap[it] = annotation.key
                true
            } else {
                false
            }
        }
        fields.forEach {
            val fieldClazz = it.type
            it.isAccessible = true
            val key = fieldKeyMap[it]!!
            it.set(this, fieldClazz.cast(findPreference(key)))
            logger.info("key: $key ,fieldName: ${it.name} 赋值完成")
        }
        logger.info("所有Preference都已经赋值完成")
        init()
    }

    protected abstract fun init()

    protected fun newBackgroundMethodDialog(
        activity: ToolboxActivity,
        pref: SwitchPreference
    ): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("注意")
            .setMessage("你没有设置耳机工具箱的后台驻留方式，请到设置页面进行设置")
            .setPositiveButton("好") { _, _ ->
                pref.isChecked = false
                activity.handler.sendEmptyMessage(ToolboxActivity.FLAG_GO_SETTINGS)
            }.setNegativeButton("算了") { _, _ ->
                pref.isChecked = false
            }
            .create()
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
                    onIgnoreBatteryOptimizationActivity(true)
                }
                -1 -> {
                    logger.info("用户同意了请求")
                    onIgnoreBatteryOptimizationActivity(false)
                }
            }
        }
    }

    protected abstract fun onIgnoreBatteryOptimizationActivity(accept: Boolean)

    @Target(AnnotationTarget.FIELD)
    annotation class BindKey(@StringRes val key: String)

    companion object {
        const val FLAG_FRAGMENT_SHOWN = 0xf002
    }
}