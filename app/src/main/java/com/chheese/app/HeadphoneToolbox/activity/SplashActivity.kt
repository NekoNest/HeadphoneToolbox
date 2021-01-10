package com.chheese.app.HeadphoneToolbox.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.util.get
import com.chheese.app.HeadphoneToolbox.util.logger
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        val freeAppDialogDisabled =
            app.sharedPreferences.get(resources, R.string.disableFreeAppDialog, false)

        if (freeAppDialogDisabled) {
            goToolboxActivity()
        } else {
            showFreeAppDialog()
        }
    }

    private fun showFreeAppDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("请注意")
            .setMessage("下载和使用耳机工具箱是完全免费的，如果您的耳机工具箱是付费购买获得，那么您已经上当受骗，请马上报警。\n耳机工具箱官方交流QQ群：594824871\n您可以到设置中关闭此提示。")
            .setCancelable(false)
            .setPositiveButton("已读，现在进入耳机工具箱") { _, _ ->
                logger.info("免费软件提示已被确认")
                goToolboxActivity()
            }.create().show()
    }

    private fun goToolboxActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ToolboxActivity::class.java))
            finish()
        }, 1000)
    }
}