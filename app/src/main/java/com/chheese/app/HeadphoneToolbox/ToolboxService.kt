package com.chheese.app.HeadphoneToolbox

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.chheese.app.HeadphoneToolbox.activity.ToolboxActivity
import com.chheese.app.HeadphoneToolbox.util.get

class ToolboxService : Service() {
    private lateinit var app: HeadphoneToolbox
    private lateinit var receiver: ToolboxBroadcastReceiver
    private lateinit var backgroundMethod: String
    private lateinit var backgroundMethods: Array<String>
    private lateinit var foregroundNotification: Notification

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        app = application as HeadphoneToolbox
        backgroundMethod = app.sharedPreferences.get(resources, R.string.backgroundMethod, "")
        backgroundMethods = resources.getStringArray(R.array.backgroundMethods)
        receiver = ToolboxBroadcastReceiver(app)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (backgroundMethods.indexOf(backgroundMethod) == 0) {
            foregroundNotification = newNotification()
            startForeground(ID_SERVICE, foregroundNotification)
        }

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
                NotificationManager.IMPORTANCE_NONE
            )
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(notifyChannel)
            NotificationCompat.Builder(this, ID_NOTIFICATION)
        } else {
            NotificationCompat.Builder(this)
        }
        var customNotifyTitle = app.sharedPreferences.getString("custom_notify_title", "")
        if (customNotifyTitle == "") {
            customNotifyTitle = "耳机工具箱正在运行"
        }
        var customNotifyMessage = app.sharedPreferences.getString("custom_notify_message", "")
        if (customNotifyMessage == "") {
            customNotifyMessage = "你可以隐藏这条通知，不会影响运行"
        }

        val pi = PendingIntent.getActivity(
            this,
            ToolboxActivity.FLAG_REQUEST_CODE,
            Intent(this, ToolboxActivity::class.java),
            PendingIntent.FLAG_ONE_SHOT
        )

        val action = NotificationCompat.Action(
            R.drawable.ic_baseline_open_in_new_24,
            "打开主页面",
            pi
        )

        return notifyBuilder
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(customNotifyTitle)
            .setContentText(customNotifyMessage)
            .addAction(action)
            .setShowWhen(false)
            .setNotificationSilent()
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        if (backgroundMethods.indexOf(backgroundMethod) == 1) {
            startService(Intent(this, this::class.java))
        }
    }

    companion object {
        const val ID_SERVICE = 0xf03
        const val ID_NOTIFICATION = "HeadphoneToolbox"
    }
}