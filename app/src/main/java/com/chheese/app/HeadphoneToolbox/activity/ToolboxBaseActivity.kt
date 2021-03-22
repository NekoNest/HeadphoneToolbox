package com.chheese.app.HeadphoneToolbox.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.util.PreferenceKeys
import com.chheese.app.HeadphoneToolbox.util.get
import com.chheese.app.HeadphoneToolbox.util.logger

abstract class ToolboxBaseActivity : BaseActivity() {
    lateinit var handler: Handler
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                onActivityResult(it.resultCode, it.data)
            }

        super.onCreate(savedInstanceState)
        val looper = Looper.getMainLooper()
        handler = Handler(looper, this::handleMessage)

        app = application as HeadphoneToolbox

        // 如果是被开关启动的，而且要求忽略电池优化
        // 就进行请求
        if (intent != null) {
            if (intent.getBooleanExtra("for_ignore_battery_optimizations", false)) {
                requestIgnoreBatteryOptimizations()
            }
        }
    }

    @CallSuper
    protected open fun handleMessage(msg: Message): Boolean {
        return when (msg.what) {
            ToolboxActivity.FLAG_PLAY_AUDIO -> {
                playMedia(msg.arg1)
                true
            }
            SHOW_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> {
                requestIgnoreBatteryOptimizations()
                true
            }
            else -> false
        }
    }

    internal fun onIgnoreBatteryOptimizationActivityReject() {
        AlertDialog.Builder(this)
            .setTitle("诶？")
            .setMessage("你拒绝了权限请求，是手滑了吗？")
            .setPositiveButton("是，再来一次") { _, _ ->
                requestIgnoreBatteryOptimizations()
            }.setNegativeButton("不，我反悔了") { _, _ ->
                onBatteryPermissionGrantFailed()
            }
            .setCancelable(false)
            .create().show()
    }

    protected abstract fun onBatteryPermissionGrantFailed()

    internal fun playMedia(@RawRes id: Int) {
        if (app.sharedPreferences.get(PreferenceKeys.SWITCH_ALLOW_PARALLEL, false)) {
            logger.info("创建播放器实例，资源ID：$id")
            var mp: MediaPlayer? = MediaPlayer.create(this, id)
            mp!!.start()
            mp.setOnCompletionListener {
                mp!!.release()
                mp = null
                logger.info("播放结束，已释放")
            }
        } else {
            if (mediaPlayer == null) {
                logger.info("创建播放器实例，资源ID：$id")
                mediaPlayer = MediaPlayer.create(this, id)
                mediaPlayer!!.start()
                mediaPlayer!!.setOnCompletionListener {
                    mediaPlayer!!.release()
                    mediaPlayer = null
                    logger.info("播放结束，已释放")
                }
            } else {
                if (mediaPlayer!!.isPlaying) {
                    Toast.makeText(this, "当前有测试音频正在播放", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    internal fun requestIgnoreBatteryOptimizations() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnored = pm.isIgnoringBatteryOptimizations(app.packageName)
        if (isIgnored) return
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.fromParts("package", app.packageName, null)
        activityResultLauncher.launch(intent)
    }

    protected abstract fun onActivityResult(resultCode: Int, data: Intent?)

    override fun onStop() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        super.onStop()
    }
}