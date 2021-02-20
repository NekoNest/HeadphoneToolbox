package com.chheese.app.HeadphoneToolbox.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.chheese.app.HeadphoneToolbox.activity.BaseActivity
import com.chheese.app.HeadphoneToolbox.activity.SplashActivity
import com.chheese.app.HeadphoneToolbox.activity.ToolboxActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.util.newMessage

/**
 * 能让LiveData进行生命周期感知的TileService
 */
@RequiresApi(Build.VERSION_CODES.N)
abstract class LifecycleTileService : TileService(), LifecycleOwner {
    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)

    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycleDispatcher.lifecycle

    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected fun requestIgnoreBatteryOptimizations() {
        var flag = Intent.FLAG_ACTIVITY_NEW_TASK
        var context: Context = this
        if (SharedAppData.topActivity.value is BaseActivity) {
            if (SharedAppData.topActivity.value is SplashActivity) {
                return
            }
            if (SharedAppData.topActivity.value is ToolboxActivity) {
                (SharedAppData.topActivity.value as ToolboxActivity)
                    .handler.sendMessage(newMessage(BaseActivity.SHOW_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS))
                return
            }
            flag = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context = SharedAppData.topActivity.value!!
        }
        val intent = Intent(this, ToolboxActivity::class.java)
        intent.putExtra("for_ignore_battery_optimizations", true)
        intent.addFlags(flag)
        context.startActivity(intent)
    }
}