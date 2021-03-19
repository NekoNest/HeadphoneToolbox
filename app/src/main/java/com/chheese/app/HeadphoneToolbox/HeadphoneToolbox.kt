package com.chheese.app.HeadphoneToolbox

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.preference.PreferenceManager
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.service.ToolboxService
import com.chheese.app.HeadphoneToolbox.util.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HeadphoneToolbox : Application(), LifecycleOwner, Application.ActivityLifecycleCallbacks {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val handler = Handler()
    private lateinit var mLastDispatchRunnable: DispatchRunnable

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        postDispatchRunnable(Lifecycle.Event.ON_CREATE)
        postDispatchRunnable(Lifecycle.Event.ON_START)
        super.onCreate()
        val fileName = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
            .format(System.currentTimeMillis()) + ".log"
        val logFile = File(filesDir, fileName)
        Runtime.getRuntime().exec("logcat -f ${logFile.absolutePath}")

        logger.info("新建Application对象，日志打印工具初始化完成")

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        logger.info("数据存储器初始化完成")

        initLiveData()

        registerActivityLifecycleCallbacks(this)
    }

    override fun onTerminate() {
        postDispatchRunnable(Lifecycle.Event.ON_STOP)
        postDispatchRunnable(Lifecycle.Event.ON_DESTROY)
        super.onTerminate()
    }

    private fun initLiveData() {
        SharedAppData.lightScreen.value =
            sharedPreferences.get(PreferenceKeys.SWITCH_LIGHT_SCREEN, false)
        SharedAppData.openPlayer.value =
            sharedPreferences.get(PreferenceKeys.SWITCH_OPEN_PLAYER, false)

        SharedAppData.lightScreen.observe(this) {
            // 检查两个功能是否都处于关闭状态
            // 如果否就启动后台服务
            checkFeatureStatus()
            sharedPreferences.edit {
                putBoolean(PreferenceKeys.SWITCH_LIGHT_SCREEN, it)
            }
        }
        SharedAppData.openPlayer.observe(this) {
            // 检查两个功能是否都处于关闭状态
            // 如果否就启动后台服务
            checkFeatureStatus()
            sharedPreferences.edit {
                putBoolean(PreferenceKeys.SWITCH_OPEN_PLAYER, it)
            }
        }
    }

    private fun checkFeatureStatus() {
        if (SharedAppData.lightScreen.value!! || SharedAppData.openPlayer.value!!) {
            logger.info("两个需要后台服务的功能中有一个处于开启状态，需要启动后台服务")
            startService()
        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, ToolboxService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun getLifecycle() = lifecycleRegistry

    private fun postDispatchRunnable(event: Lifecycle.Event) {
        if (this::mLastDispatchRunnable.isInitialized) {
            mLastDispatchRunnable.run()
        }
        mLastDispatchRunnable = DispatchRunnable(lifecycleRegistry, event)
        handler.postAtFrontOfQueue(mLastDispatchRunnable)
    }

    internal class DispatchRunnable(
        private val mRegistry: LifecycleRegistry,
        private val mEvent: Lifecycle.Event
    ) : Runnable {
        private var mWasExecuted = false
        override fun run() {
            if (!mWasExecuted) {
                mRegistry.handleLifecycleEvent(mEvent)
                mWasExecuted = true
            }
        }
    }

    // 以下是应用内Activity生命周期回调
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        logger.verbose("${activity::class.java.simpleName}: onCreate")
    }

    override fun onActivityStarted(activity: Activity) {
        logger.verbose("${activity::class.java.simpleName}: onStart")
    }

    override fun onActivityResumed(activity: Activity) {
        logger.verbose("${activity::class.java.simpleName}: onResumed")
        logger.info(activity::class.java.simpleName + "目前置于顶层")
        SharedAppData.topActivity setTo activity
    }

    override fun onActivityPaused(activity: Activity) {
        logger.verbose("${activity::class.java.simpleName}: onPause")
    }

    override fun onActivityStopped(activity: Activity) {
        logger.verbose("${activity::class.java.simpleName}: onStopped")
        logger.info(activity::class.java.simpleName + "已终止")
        if (SharedAppData.topActivity.value == activity) {
            logger.info("现在没有【耳机工具箱】的Activity置于顶层")
            SharedAppData.topActivity setTo null
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        logger.verbose("${activity::class.java.simpleName}: onSaveInstance")
    }

    override fun onActivityDestroyed(activity: Activity) {
        logger.verbose("${activity::class.java.simpleName}: onDestroyed")
    }
}