package com.chheese.app.HeadphoneToolbox.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.isVisible
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.util.PreferenceKeys
import com.chheese.app.HeadphoneToolbox.util.checkPermissions
import com.chheese.app.HeadphoneToolbox.util.get
import com.chheese.app.HeadphoneToolbox.util.logger
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val text = findViewById<MaterialTextView>(R.id.text_experimental_feature)

        val isExperimentalFeatureEnabled =
            app.sharedPreferences.get(PreferenceKeys.SWITCH_USE_EXPERIMENTAL_FEATURE, false)
        text.isVisible = isExperimentalFeatureEnabled

        val freeAppDialogDisabled =
            app.sharedPreferences.get(PreferenceKeys.SWITCH_DISABLE_FREE_APP_DIALOG, false)

        if (freeAppDialogDisabled) {
            checkPermissionsInternal()
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
                checkPermissionsInternal()
            }.create().show()
    }

    private fun goToolboxActivity() {
        val useNewUi = app.sharedPreferences.get(PreferenceKeys.SWITCH_USE_NEW_UI, false)
        val cls = if (useNewUi) MainActivity::class.java else ToolboxActivity::class.java
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, cls))
            finish()
        }, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logger.info("resultCode: $resultCode")
        if (resultCode == PermissionManageActivity.RESULT_RETURN_FROM_GRANT_PERMISSION_ACTIVITY) {
            logger.info("从授权页面返回")
            goToolboxActivity()
        }
    }

    private fun checkPermissionsInternal() {
        checkPermissions(
            permissionAllGranted = {
                goToolboxActivity()
            },
            onPositiveButtonClick = {
                startActivityForResult(Intent(this, PermissionManageActivity::class.java), 0)
            },
            onNegativeButtonClick = {
                goToolboxActivity()
            },
            onNeutralButtonClick = {
                finish()
            }
        )
    }
}