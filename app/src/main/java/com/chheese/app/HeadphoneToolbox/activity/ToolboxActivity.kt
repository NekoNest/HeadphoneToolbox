package com.chheese.app.HeadphoneToolbox.activity

import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.Settings
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.fragment.BaseFragment
import com.chheese.app.HeadphoneToolbox.fragment.MainFragment
import com.chheese.app.HeadphoneToolbox.fragment.SettingsFragment
import com.chheese.app.HeadphoneToolbox.util.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class ToolboxActivity : ToolboxBaseActivity() {
    private val mainFragment = MainFragment()
    private val settingsFragment = SettingsFragment()
    private var currentFragment: BaseFragment? = null

    private lateinit var mainNav: BottomNavigationView
    private lateinit var mainToolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_toolbox)

        mainNav = findViewById(R.id.nav_main)
        mainToolbar = findViewById(R.id.toolbar_main)

        setSupportActionBar(mainToolbar)
        supportActionBar!!.title = ""

        if (app.sharedPreferences.get(PreferenceKeys.PREF_OPEN_DETAILS, false)) {
            logger.info("打开设置中的应用详情页")
            val intent = Intent()
                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.fromParts("package", packageName, null))
            startActivity(intent)
        }

        mainNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.main, R.id.settings -> {
                    selectedBottomNavId setTo it.itemId
                    true
                }
                else -> false
            }
        }

        selectedBottomNavId.observe(this, this::switchFragment)
    }

    override fun onResume() {
        super.onResume()
        if (selectedBottomNavId.value == 0) {
            selectedBottomNavId.value = R.id.main
        } else {
            selectedBottomNavId.value = selectedBottomNavId.value!!
        }
    }

    private fun beginFragmentTransaction(block: FragmentTransaction.() -> Unit) {
        val transaction = supportFragmentManager.beginTransaction()
        block(transaction)
        transaction.commit()
    }

    private fun switchFragment(@IdRes id: Int) {
        when (id) {
            R.id.main -> switchFragment(mainFragment)
            R.id.settings -> switchFragment(settingsFragment)
            else -> logger.warn("你传了什么值进来了？$id 是什么？")
        }
    }

    private fun switchFragment(fragment: BaseFragment) {
        logger.info("要切换到${fragment}")
        beginFragmentTransaction {
            if (currentFragment == null) {
                if (fragment.isAdded) {
                    show(fragment)
                    this@ToolboxActivity.logger.verbose("添加了${fragment}")
                } else {
                    add(R.id.frame_main, fragment)
                    this@ToolboxActivity.logger.verbose("添加了${fragment}")
                }
            } else {
                hide(currentFragment!!)
                if (fragment.isAdded) {
                    show(fragment)
                    this@ToolboxActivity.logger.verbose("添加了${fragment}")
                } else {
                    add(R.id.frame_main, fragment)
                    this@ToolboxActivity.logger.verbose("添加了${fragment}")
                }
            }
            fragment.handler.sendEmptyMessage(BaseFragment.FLAG_FRAGMENT_SHOWN)
            currentFragment = fragment
        }
    }

    override fun onStop() {
        // 停止Activity时隐藏Fragment以免出现重影
        if (currentFragment != null) {
            beginFragmentTransaction {
                supportFragmentManager.fragments.forEach {
                    hide(it) // 请hide而不是remove
                }
            }
        }
        super.onStop()
    }

    override fun onActivityResult(resultCode: Int, data: Intent?) {
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

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == FLAG_GO_SETTINGS) {
            if (this::mainNav.isInitialized) {
                mainNav.selectedItemId = R.id.settings
            }
            return true
        }
        return super.handleMessage(msg)
    }

    override fun onBatteryPermissionGrantFailed() {
        mainFragment.handler.sendEmptyMessage(BaseFragment.REQUEST_IGNORE_BATTERY_OPTIMIZATION_FAILED)
    }

    companion object {
        const val FLAG_GO_SETTINGS = 0xf000
        const val FLAG_REQUEST_CODE = 0xf004
        const val FLAG_PLAY_AUDIO = 0xf006
        val selectedBottomNavId = MutableLiveData(0)
    }
}