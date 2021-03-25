package com.chheese.app.HeadphoneToolbox.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel

/**
 * 授权指引卡片
 * @param name 权限名称
 * @param description 权限描述
 * @param affectingFeatures 该权限会影响到的功能
 * @param isGranted 权限是否已经授予，如果已经授予，卡片会处于激活状态，并且包含对勾
 * @param onClick 当点击卡片时需要进行的操作
 */
@Composable
fun PermissionManageCard(
    isGranted: Boolean,
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    affectingFeatures: String,
    viewModel: ToolboxViewModel,
    onClick: () -> Unit = {},
) {
    TwoStateCard(
        isActive = isGranted,
        viewModel = viewModel,
        modifier = modifier,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "完成",
                tint = animateColorAsState(
                    targetValue = if (isGranted) {
                        iconAndTextColor(viewModel, true)
                    } else {
                        Color.Transparent
                    }
                ).value,
                modifier = Modifier.size(28.dp)
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = name,
                    color = animateColorAsState(
                        targetValue = iconAndTextColor(viewModel, isGranted)
                    ).value,
                    fontSize = 20.sp
                )
                Text(
                    text = description,
                    color = animateColorAsState(
                        targetValue = iconAndTextColor(viewModel, isGranted)
                    ).value,
                    fontSize = 12.sp
                )
                Text(
                    text = "会影响到的功能：$affectingFeatures",
                    color = animateColorAsState(
                        targetValue = iconAndTextColor(viewModel, isGranted)
                    ).value,
                    fontSize = 12.sp
                )
                Text(
                    text = "点击进入相应授权页面",
                    color = animateColorAsState(
                        targetValue = iconAndTextColor(viewModel, isGranted)
                    ).value,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermissionManageCardPreview() {
    PermissionManageCard(
        isGranted = false,
        name = "调用系统级对话框",
        description = "在后台为您启动第三方APP时需要此权限",
        affectingFeatures = "插入或连接耳机时打开播放器",
        viewModel = ToolboxViewModel()
    )
}

@Preview(showBackground = true)
@Composable
fun PermissionManageCardPreview2() {
    PermissionManageCard(
        isGranted = true,
        name = "调用系统级对话框",
        description = "在后台为您启动第三方APP时需要此权限",
        affectingFeatures = "插入或连接耳机时打开播放器",
        viewModel = ToolboxViewModel()
    )
}