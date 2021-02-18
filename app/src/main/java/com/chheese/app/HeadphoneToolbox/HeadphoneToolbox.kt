package com.chheese.app.HeadphoneToolbox

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.chheese.app.HeadphoneToolbox.util.get
import com.chheese.app.HeadphoneToolbox.util.logger
import com.google.android.gms.ads.MobileAds
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HeadphoneToolbox : Application() {
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        val fileName = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
            .format(System.currentTimeMillis()) + ".log"
        val logFile = File(filesDir, fileName)
        Runtime.getRuntime().exec("logcat -f ${logFile.absolutePath}")

        logger.info("新建Application对象，日志打印工具初始化完成")

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        logger.info("数据存储器初始化完成")

        val isFeatureEnabled =
            sharedPreferences.get(resources, R.string.enableExperimentalFeature, false)

        if (isFeatureEnabled) {
            MobileAds.initialize(this) {
                val statuses = it.adapterStatusMap
                for (entry in statuses.entries) {
                    logger.info("广告适配器：${entry.key}，初始化状态：${entry.value.initializationState}")
                }
            }
        }
    }
}