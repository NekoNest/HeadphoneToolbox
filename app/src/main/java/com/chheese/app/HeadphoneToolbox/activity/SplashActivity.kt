package com.chheese.app.HeadphoneToolbox.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.get
import com.google.android.material.textview.MaterialTextView

class SplashActivity : NoActionBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val text = findViewById<MaterialTextView>(R.id.text_experimental_feature)

        val isFeatureEnabled =
            app.sharedPreferences.get(resources, R.string.enableExperimentalFeature, false)

        if (isFeatureEnabled) {
            text.visibility = View.VISIBLE
        } else {
            text.visibility = View.INVISIBLE
        }

        Handler().postDelayed({
            startActivity(Intent(this, ToolboxActivity::class.java))
            finish()
        }, 1000)
    }
}