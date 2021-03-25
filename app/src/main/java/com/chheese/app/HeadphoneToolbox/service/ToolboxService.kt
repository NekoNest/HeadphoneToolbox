package com.chheese.app.HeadphoneToolbox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.ToolboxBroadcastReceiver
import com.chheese.app.HeadphoneToolbox.activity.MainActivity
import com.chheese.app.HeadphoneToolbox.activity.ToolboxActivity
import com.chheese.app.HeadphoneToolbox.activity.ToolboxBaseActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.util.PreferenceKeys
import com.chheese.app.HeadphoneToolbox.util.get
import com.chheese.app.HeadphoneToolbox.util.logger
import com.chheese.app.HeadphoneToolbox.util.setTo

class ToolboxService : LifecycleService() {
    private lateinit var app: HeadphoneToolbox
    private lateinit var receiver: ToolboxBroadcastReceiver
    private lateinit var foregroundNotification: Notification

    override fun onCreate() {
        super.onCreate()
        app = application as HeadphoneToolbox
        receiver = ToolboxBroadcastReceiver(app)
        // 当功能开关状态变化时
        // 检查功能开关是否都处于关闭状态
        // 如果是则自动停止服务
        SharedAppData.lightScreen.observe(this) {
            checkFeatureStatus()
        }
        SharedAppData.openPlayer.observe(this) {
            checkFeatureStatus()
        }
        SharedAppData.isServiceRunning setTo true
    }

    /**
     * 具体的功能开关状态检查函数
     */
    private fun checkFeatureStatus() {
        if (SharedAppData.lightScreen.value!! || SharedAppData.openPlayer.value!!) {
            return
        }
        logger.info("两个需要后台服务的功能都处于关闭状态，需要停止后台服务")
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        foregroundNotification = newNotification()
        startForeground(ID_SERVICE, foregroundNotification)

        val intentFilter = IntentFilter(Intent.ACTION_HEADSET_PLUG)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        registerReceiver(receiver, intentFilter)
        return super.onStartCommand(intent, flags, startId)
    }

    @Suppress("DEPRECATION")
    private fun newNotification(): Notification {
        val notifyBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notifyChannel = NotificationChannel(
                ID_NOTIFICATION,
                ID_NOTIFICATION,
                NotificationManager.IMPORTANCE_HIGH
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(notifyChannel)
            NotificationCompat.Builder(this, ID_NOTIFICATION)
        } else {
            NotificationCompat.Builder(this)
        }
        val notifyTitle = "耳机工具箱正在运行"
        val notifyMessage = "你可以隐藏这条通知，不会影响运行"

        val isNewUiEnabled = app.sharedPreferences.get(PreferenceKeys.SWITCH_USE_NEW_UI, true)
        val mainActivityCls: Class<out ToolboxBaseActivity> = if (isNewUiEnabled) {
            MainActivity::class.java
        } else {
            ToolboxActivity::class.java
        }

        val pi = PendingIntent.getActivity(
            this,
            ToolboxActivity.FLAG_REQUEST_CODE,
            Intent(this, mainActivityCls),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val action = NotificationCompat.Action(
            R.drawable.ic_open_player,
            "打开主页面",
            pi
        )

        return notifyBuilder
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notifyTitle)
            .setContentText(notifyMessage)
            .addAction(action)
            .setShowWhen(false)
            .setNotificationSilent()
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        SharedAppData.isServiceRunning setTo false
    }

    companion object {
        const val ID_SERVICE = 0xf03
        const val ID_NOTIFICATION = "HeadphoneToolbox"
    }
}