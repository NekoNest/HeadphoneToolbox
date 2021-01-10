package com.chheese.app.HeadphoneToolbox.activity

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.fragment.app.FragmentTransaction
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.ToolboxService
import com.chheese.app.HeadphoneToolbox.fragment.AbstractPreferenceFragment
import com.chheese.app.HeadphoneToolbox.fragment.MainFragment
import com.chheese.app.HeadphoneToolbox.fragment.SettingsFragment
import com.chheese.app.HeadphoneToolbox.util.get
import com.chheese.app.HeadphoneToolbox.util.logger
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class ToolboxActivity : NoActionBarActivity() {
    private val mainFragment = MainFragment()
    private val settingsFragment = SettingsFragment()
    private var currentFragment: AbstractPreferenceFragment? = null
    lateinit var handler: Handler

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var mainNav: BottomNavigationView
    private lateinit var mainToolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val looper = Looper.getMainLooper()
        handler = Handler(looper) {
            when (it.what) {
                FLAG_GO_SETTINGS -> {
                    if (this::mainNav.isInitialized) {
                        mainNav.selectedItemId = R.id.settings
                    }
                    true
                }
                FLAG_PLAY_AUDIO -> {
                    playMedia(it.arg1)
                    true
                }
                else -> false
            }
        }

        app = application as HeadphoneToolbox

        setContentView(R.layout.activity_toolbox)

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

        startService()
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

    private fun startService() {
        val backgroundMethod =
            app.sharedPreferences.get(resources, R.string.backgroundMethod, "")
        val backgroundMethods = resources.getStringArray(R.array.backgroundMethods)
        val serviceIntent = Intent(this, ToolboxService::class.java)
        if (backgroundMethods.indexOf(backgroundMethod) == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        } else if (backgroundMethod == backgroundMethods[1]) {
            startService(serviceIntent)
        }
    }

    private fun beginFragmentTransaction(block: FragmentTransaction.() -> Unit) {
        val transaction = supportFragmentManager.beginTransaction()
        block(transaction)
        transaction.commit()
    }

    private fun switchFragment(fragment: AbstractPreferenceFragment) {
        beginFragmentTransaction {
            if (currentFragment == null) {
                if (fragment.isAdded) {
                    show(fragment)
                } else {
                    add(R.id.frame_main, fragment)
                }
            } else {
                hide(currentFragment!!)
                if (fragment.isAdded) {
                    show(fragment)
                } else {
                    add(R.id.frame_main, fragment)
                }
            }
            fragment.handler.sendEmptyMessage(AbstractPreferenceFragment.FLAG_FRAGMENT_SHOWN)
            currentFragment = fragment
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    companion object {
        const val FLAG_GO_SETTINGS = 0xf000
        const val FLAG_REQUEST_CODE = 0xf004
        const val FLAG_PLAY_AUDIO = 0xf006
    }
}