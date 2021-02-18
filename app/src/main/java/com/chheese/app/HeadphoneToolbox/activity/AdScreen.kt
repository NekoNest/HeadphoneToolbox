package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.chheese.app.HeadphoneToolbox.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.appbar.MaterialToolbar

class AdScreen : NoActionBarActivity() {
    private lateinit var noGmsHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
        val showSwitch = findViewById<SwitchCompat>(R.id.switch_show_ads)
        val clickSwitch = findViewById<SwitchCompat>(R.id.switch_click_ads)
        val ad1 = findViewById<AdView>(R.id.app_ad1)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_ad)
        noGmsHint = findViewById(R.id.no_gms_hint)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        ad1.isVisible = false
        ad1.isFocusable = false
        ad1.isClickable = false
        noGmsHint.isVisible = !checkGms()

        showSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            ad1.isVisible = isChecked
            if (isChecked) {
                val adRequest = AdRequest.Builder().build()
                ad1.loadAd(adRequest)
            }
        }

        clickSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            ad1.isFocusable = isChecked
            ad1.isClickable = isChecked
        }
    }

    private fun checkGms(): Boolean {
        val gms = packageManager.getInstalledApplications(0).find {
            it.packageName == "com.google.android.gms"
        }
        return gms != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}