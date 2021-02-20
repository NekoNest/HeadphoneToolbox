package com.chheese.app.HeadphoneToolbox.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentTransaction
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.fragment.BaseFragment
import com.chheese.app.HeadphoneToolbox.fragment.MainFragment
import com.chheese.app.HeadphoneToolbox.fragment.SettingsFragment
import com.chheese.app.HeadphoneToolbox.util.get
import com.chheese.app.HeadphoneToolbox.util.logger
import com.chheese.app.HeadphoneToolbox.util.newMessage
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class ToolboxActivity : BaseActivity() {
    private val mainFragment = MainFragment()
    private val settingsFragment = SettingsFragment()
    private var currentFragmentParent: BaseFragment? = null
    lateinit var handler: Handler

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var mainNav: BottomNavigationView
    private lateinit var mainToolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val looper = Looper.getMainLooper()
        handler = Handler(looper, this::handleMessage)

        app = application as HeadphoneToolbox

        setContentView(R.layout.activity_toolbox)

        // 如果是被开关启动的，而且要求忽略电池优化
        // 就进行请求
        if (intent != null) {
            if (intent.getBooleanExtra("for_ignore_battery_optimizations", false)) {
                requestIgnoreBatteryOptimizations()
            }
        }

        mainNav = findViewById(R.id.nav_main)
        mainToolbar = findViewById(R.id.toolbar_main)

        setSupportActionBar(mainToolbar)
        supportActionBar!!.title = ""
        switchFragment(mainFragment)
        mainNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main -> {
                    switchFragment(mainFragment)
                    true
                }
                R.id.settings -> {
                    switchFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }

        if (app.sharedPreferences.get(resources, R.string.openDetails, false)) {
            logger.info("打开设置中的应用详情页")
            val intent = Intent()
                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
            startActivity(intent)
        }
    }

    private fun handleMessage(msg: Message): Boolean {
        return when (msg.what) {
            FLAG_GO_SETTINGS -> {
                if (this::mainNav.isInitialized) {
                    mainNav.selectedItemId = R.id.settings
                }
                true
            }
            FLAG_PLAY_AUDIO -> {
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

    private fun playMedia(@RawRes id: Int) {
        if (app.sharedPreferences.get(resources, R.string.allowParallel, false)) {
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
    private fun requestIgnoreBatteryOptimizations() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val isIgnored = pm.isIgnoringBatteryOptimizations(app.packageName)
        if (isIgnored) return
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.fromParts("package", app.packageName, null)
        startActivityForResult(intent, SettingsFragment.FLAG_IGNORE_BATTERY_OPTIMIZATIONS)
    }

    private fun beginFragmentTransaction(block: FragmentTransaction.() -> Unit) {
        val transaction = supportFragmentManager.beginTransaction()
        block(transaction)
        transaction.commit()
    }

    private fun switchFragment(fragmentParent: BaseFragment) {
        beginFragmentTransaction {
            if (currentFragmentParent == null) {
                if (fragmentParent.isAdded) {
                    show(fragmentParent)
                } else {
                    add(R.id.frame_main, fragmentParent)
                }
            } else {
                hide(currentFragmentParent!!)
                if (fragmentParent.isAdded) {
                    show(fragmentParent)
                } else {
                    add(R.id.frame_main, fragmentParent)
                }
            }
            fragmentParent.handler.sendEmptyMessage(BaseFragment.FLAG_FRAGMENT_SHOWN)
            currentFragmentParent = fragmentParent
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SettingsFragment.FLAG_IGNORE_BATTERY_OPTIMIZATIONS) {
            when (resultCode) {
                0 -> {
                    logger.info("用户拒绝了请求")
                    mainFragment.handler.sendMessage(newMessage(BaseFragment.REQUEST_IGNORE_BATTERY_OPTIMIZATION_FAILED))
                }
                -1 -> {
                    logger.info("用户同意了请求")
                }
            }
        }
    }

    companion object {
        const val FLAG_GO_SETTINGS = 0xf000
        const val FLAG_REQUEST_CODE = 0xf004
        const val FLAG_PLAY_AUDIO = 0xf006
    }
}