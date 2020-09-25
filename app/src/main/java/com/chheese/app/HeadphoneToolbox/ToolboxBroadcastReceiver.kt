package com.chheese.app.HeadphoneToolbox

import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.view.WindowManager
import android.widget.Toast

class ToolboxBroadcastReceiver(private val app: HeadphoneToolbox) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val extra = intent?.extras ?: return
        val state = extra.getInt("state", -1)
        if (state == 0) {
            app.logger.info("耳机已拔出")
        } else if (state == 1) {
            app.logger.info("耳机已插入")
            if (app.sharedPreferences.get(context.resources, R.string.lightScreen, false)) {
                requireWakelock(context)
            }
            if (app.sharedPreferences.get(context.resources, R.string.openPlayer, false)) {
                if (app.sharedPreferences.get(
                        context.resources, R.string.alertOnOpen, false
                    )
                ) {
                    showOpenDialog(app)
                } else {
                    openPlayer(app)
                }
            }
        }
        if (app.sharedPreferences.get(app.resources, R.string.allowBluetooth, false)) {
            if (extra.getInt("android.bluetooth.adapter.extra.CONNECTION_STATE", -1) == 2
                || extra.getInt("android.bluetooth.adapter.extra.STATE", -1) == 12
            ) {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                var hasAudio = false

                bluetoothAdapter.bondedDevices.forEach {
                    if (it.bluetoothClass.majorDeviceClass == BluetoothClass.Device.Major.AUDIO_VIDEO) {
                        app.logger.info("蓝牙耳机已连接")
                        hasAudio = true
                        return@forEach
                    }
                }
                if (!hasAudio) return
                if (app.sharedPreferences.get(context.resources, R.string.openPlayer, false)) {
                    if (app.sharedPreferences.get(
                            context.resources, R.string.alertOnOpen, false
                        )
                    ) {
                        showOpenDialog(app)
                    } else {
                        openPlayer(app)
                    }
                }
            }
        }
    }

    private fun showOpenDialog(app: HeadphoneToolbox) {
        val dialog = AlertDialog.Builder(
            app,
            R.style.AppTheme_Dialog
        ).setTitle("请问一下")
            .setMessage("要打开播放器吗？")
            .setPositiveButton("好的好的") { _, _ ->
                openPlayer(app)
            }.setNegativeButton("才不要呢") { _, _ -> }
            .create()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dialog.window!!.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else {
            dialog.window!!.setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG)
        }
        dialog.show()
    }

    private fun openPlayer(app: HeadphoneToolbox) {
        val selectedPlayer = app.sharedPreferences.get(app.resources, R.string.selectPlayer, "")
        if (selectedPlayer == "") {
            Toast.makeText(app, "没有选择播放器哦", Toast.LENGTH_LONG).show()
        } else {
            val launchIntent = app.packageManager.getLaunchIntentForPackage(selectedPlayer)
            try {
                app.startActivity(launchIntent)
            } catch (e: NullPointerException) {
                Toast.makeText(app, "当前选择的播放器可能没有界面", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requireWakelock(ctx: Context) {
        val pm = ctx.getSystemService(PowerManager::class.java)
        val wakelock = pm.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_DIM_WAKE_LOCK,
            "HeadphoneToolbox:wakelock"
        )
        wakelock.acquire(10 * 60 * 1000L)
        wakelock.release()
    }
}