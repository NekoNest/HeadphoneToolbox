package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import com.chheese.app.HeadphoneToolbox.ui.UiSettings
import com.chheese.app.HeadphoneToolbox.ui.theme.ToolboxTheme

class UiSettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ToolboxTheme(
                content = {
                    UiSettings(viewModel = viewModel)
                },
                viewModel = viewModel
            )
        }
    }
}