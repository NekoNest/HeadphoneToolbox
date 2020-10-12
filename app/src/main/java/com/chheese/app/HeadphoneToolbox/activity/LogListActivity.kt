package com.chheese.app.HeadphoneToolbox.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import com.chheese.app.HeadphoneToolbox.HeadphoneToolbox
import com.chheese.app.HeadphoneToolbox.R
import com.google.android.material.appbar.MaterialToolbar
import java.io.File

class LogListActivity : NoActionBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_logs)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_logs)
        val logList = findViewById<ListView>(R.id.list_logs)

        app = application as HeadphoneToolbox
        app.logger.info("有人来看log了")

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            title = "日志列表"
        }

        val fileNames = ArrayList<Map<String, String>>()
        filesDir.listFiles()?.filter {
            it.name.endsWith(".log")
        }?.forEach {
            fileNames.add(mapOf("fileName" to it.name))
        }

        app.logger.info("log扫描完成")

        if (fileNames.isNotEmpty()) {
            logList.adapter = SimpleAdapter(
                this,
                fileNames,
                android.R.layout.simple_list_item_1, arrayOf("fileName"),
                intArrayOf(android.R.id.text1)
            )
            logList.setOnItemClickListener { _, _, position, _ ->
                val fileName = fileNames[position]["fileName"]
                val intent = Intent(this, ViewLogActivity::class.java)
                intent.putExtra("filePath", filesDir.absolutePath + File.separator + fileName)
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.clear_log -> {
                filesDir.listFiles()?.forEach {
                    it.delete()
                }
                Toast.makeText(this, "已清除所有日志", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.menu_log, menu)
        return super.onCreateOptionsMenu(menu)
    }
}