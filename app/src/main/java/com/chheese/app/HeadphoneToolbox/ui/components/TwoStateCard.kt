package com.chheese.app.HeadphoneToolbox.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel

/**
 * 具有两种状态的卡片，激活，和非激活
 * @param isActive 是否处于激活状态
 */
@Composable
fun TwoStateCard(
    isActive: Boolean,
    viewModel: ToolboxViewModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = 0.dp,
        border = BorderStroke(
            1.dp,
            color = animateColorAsState(targetValue = borderColor(viewModel, isActive)).value
        ),
        shape = viewModel.shape.value
    ) {
        Box(
            Modifier.clickable {
                onClick()
            },
            content = content
        )
    }
}

internal fun iconAndTextColor(
    viewModel: ToolboxViewModel,
    isActive: Boolean
) = if (isActive) {
    viewModel.theme.value.primary
} else {
    if (viewModel.theme.value.isLight) {
        Color.DarkGray
    } else {
        Color.LightGray
    }
}

internal fun borderColor(
    viewModel: ToolboxViewModel,
    isActive: Boolean
) = if (isActive) {
    viewModel.theme.value.primary
} else {
    if (viewModel.theme.value.isLight) {
        Color.LightGray
    } else {
        Color.DarkGray
    }
}