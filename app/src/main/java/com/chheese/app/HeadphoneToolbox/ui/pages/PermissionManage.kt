package com.chheese.app.HeadphoneToolbox.ui.pages

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chheese.app.HeadphoneToolbox.activity.PermissionManageActivity
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel
import com.chheese.app.HeadphoneToolbox.ui.components.PermissionManageCard
import com.chheese.app.HeadphoneToolbox.ui.components.ToolboxAppBar

@Composable
fun PermissionManage(
    viewModel: ToolboxViewModel,
    activity: PermissionManageActivity? = null
) {
    Column {
        ToolboxAppBar(
            title = "授权管理",
            backgroundColor = viewModel.theme.value.background,
            showBackIcon = true,
            onBackClick = {
                activity?.finish()
            }
        )
        Divider()
        PermissionManageCard(
            isGranted = viewModel.isIgnoreBatteryOptimization.value,
            name = "忽略电池优化",
            description = "我们需要持续在后台运行以接收耳机插入时系统发送的广播",
            affectingFeatures = "点亮屏幕，打开播放器",
            viewModel = viewModel,
            modifier = Modifier.padding(
                start = 8.dp, top = 8.dp, end = 8.dp
            ),
            onClick = onClick@{
                activity ?: return@onClick
                if (viewModel.isIgnoreBatteryOptimization.value) {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    activity.startActivity(intent)
                } else {
                    activity.requestIgnoreBatteryOptimizations()
                }
            }
        )
        PermissionManageCard(
            isGranted = viewModel.isAllowSystemDialog.value,
            name = "调用系统级对话框",
            description = "由于系统限制，我们需要此权限才能在后台为您打开页面",
            affectingFeatures = "打开播放器",
            viewModel = viewModel,
            modifier = Modifier.padding(
                start = 8.dp, top = 8.dp, end = 8.dp
            ),
            onClick = onClick@{
                activity ?: return@onClick
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.fromParts("package", activity.packageName, null)
                activity.startActivity(intent)
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun GrantPermissionPreview() {
    PermissionManage(viewModel = ToolboxViewModel())
}