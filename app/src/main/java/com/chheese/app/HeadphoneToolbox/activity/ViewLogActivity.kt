package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import android.view.MenuItem
import com.chheese.app.HeadphoneToolbox.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import java.io.File

class ViewLogActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        val filePath = intent.getStringExtra("filePath") ?: return
        val file = File(filePath)

        val logText = findViewById<MaterialTextView>(R.id.text_log)
        val logToolbar = findViewById<MaterialToolbar>(R.id.toolbar_log)

        logText.text = file.readText()
        setSupportActionBar(logToolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            title = file.name
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}