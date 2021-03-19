package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import android.view.MenuItem
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.fragment.SettingsFragment
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, SettingsFragment())
            .commit()

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}