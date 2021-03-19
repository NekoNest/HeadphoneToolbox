package com.chheese.app.HeadphoneToolbox.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel

/**
 * 工具箱的主题
 */
@Composable
fun ToolboxTheme(
    content: @Composable () -> Unit,
    viewModel: ToolboxViewModel
) {
    MaterialTheme(
        content = content,
        colors = viewModel.theme.value
    )
}

object ToolboxTheme {
    val light = lightColors()
    val dark = darkColors()
}