package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import android.view.MenuItem
import com.chheese.app.HeadphoneToolbox.R
import kotlinx.android.synthetic.main.activity_log.*
import java.io.File

class ViewLogActivity : NoActionBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        val filePath = intent.getStringExtra("filePath") ?: return
        val file = File(filePath)
        text_log.text = file.readText()
        setSupportActionBar(toolbar_log)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            title = file.name
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}