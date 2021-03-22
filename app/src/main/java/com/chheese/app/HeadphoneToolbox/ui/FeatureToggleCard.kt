package com.chheese.app.HeadphoneToolbox.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel

@Composable
fun FeatureToggleCard(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    title: String,
    viewModel: ToolboxViewModel,
    @DrawableRes iconId: Int = 0,
    imageVector: ImageVector? = null,
    onClick: () -> Unit = {},
    showSettings: Boolean = false,
    onSettingsClick: () -> Unit = {}
) {
    val iconAndTextColor = if (isActive) {
        viewModel.theme.value.primary
    } else {
        if (viewModel.theme.value.isLight) {
            Color.DarkGray
        } else {
            Color.LightGray
        }
    }
    val borderColor = if (isActive) {
        viewModel.theme.value.primary
    } else {
        if (viewModel.theme.value.isLight) {
            Color.LightGray
        } else {
            Color.DarkGray
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        elevation = 0.dp,
        border = BorderStroke(1.dp, color = animateColorAsState(targetValue = borderColor).value),
        shape = viewModel.shape.value
    ) {
        Box {
            // 将图标包裹起来，让图标的可点击范围不那么小
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable {
                        if (showSettings) {
                            onSettingsClick()
                        }
                    }
            ) {
                Icon(
                    tint = animateColorAsState(targetValue = iconAndTextColor).value,
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "",
                    modifier = Modifier
                        .size(34.dp)
                        .padding(8.dp)
                        .alpha(
                            animateFloatAsState(
                                targetValue = if (showSettings) {
                                    1f
                                } else {
                                    0f
                                }
                            ).value
                        )
                )
            }
            Column(modifier = Modifier.padding(8.dp)) {
                if (iconId != 0) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(16.dp),
                        tint = animateColorAsState(targetValue = iconAndTextColor).value
                    )
                } else if (imageVector != null) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(16.dp),
                        tint = animateColorAsState(targetValue = iconAndTextColor).value
                    )
                }
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = animateColorAsState(targetValue = iconAndTextColor).value
                )
            }
        }
    }
}

/**
 * 边角半径，依次是 左上角，右上角，左下角和右下角
 */
data class ShapeCornerSize(
    val topStart: Dp,
    val topEnd: Dp,
    val bottomStart: Dp,
    val bottomEnd: Dp
)

/**
 * 形状类型
 */
enum class ShapeType {
    /**
     * 没有形状，卡片和按钮等的边角将显示为直角
     */
    NONE,

    /**
     * 圆形边角，需要设置半径
     */
    ROUNDED,

    /**
     * 像钻石切边一样的边角，需要设置半径
     */
    CUT
}

/**
 * 创建形状，用于卡片，按钮等
 */
fun shape(type: ShapeType, shapeCornerSize: ShapeCornerSize): Shape {
    val (topStart, topEnd, bottomStart, bottomEnd) = shapeCornerSize
    return when (type) {
        ShapeType.NONE -> RoundedCornerShape(0.dp)
        ShapeType.CUT -> CutCornerShape(topStart, topEnd, bottomEnd, bottomStart)
        ShapeType.ROUNDED -> RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart)
    }
}

@Preview(showBackground = true)
@Composable
fun FeatureToggleCardPreview() {
    FeatureToggleCard(
        imageVector = Icons.Filled.Screenshot,
        isActive = false,
        title = "屏幕截图",
        viewModel = ToolboxViewModel(),
        showSettings = true
    )
}