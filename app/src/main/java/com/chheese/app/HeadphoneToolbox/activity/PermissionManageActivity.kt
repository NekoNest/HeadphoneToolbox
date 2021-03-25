package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import com.chheese.app.HeadphoneToolbox.ui.pages.PermissionManage
import com.chheese.app.HeadphoneToolbox.ui.theme.ToolboxTheme
import com.chheese.app.HeadphoneToolbox.util.isIgnoringBatteryOptimizations

class PermissionManageActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToolboxTheme(
                viewModel = viewModel,
                content = {
                    PermissionManage(
                        viewModel = viewModel,
                        activity = this
                    )
                }
            )
        }
        setResult(RESULT_RETURN_FROM_GRANT_PERMISSION_ACTIVITY)
    }

    override fun onResume() {
        super.onResume()
        viewModel.isIgnoreBatteryOptimization.value = isIgnoringBatteryOptimizations()
        viewModel.isAllowSystemDialog.value = Settings.canDrawOverlays(this)
    }

    companion object {
        const val RESULT_RETURN_FROM_GRANT_PERMISSION_ACTIVITY = 114
    }
}