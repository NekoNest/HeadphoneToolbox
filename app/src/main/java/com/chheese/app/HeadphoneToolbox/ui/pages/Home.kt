package com.chheese.app.HeadphoneToolbox.ui.pages

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.activity.MainActivity
import com.chheese.app.HeadphoneToolbox.activity.PermissionManageActivity
import com.chheese.app.HeadphoneToolbox.activity.SettingsActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel
import com.chheese.app.HeadphoneToolbox.ui.components.FeatureToggleCard
import com.chheese.app.HeadphoneToolbox.ui.components.ToolboxAppBar
import com.chheese.app.HeadphoneToolbox.util.*

@ExperimentalFoundationApi
@Composable
fun Home(
    viewModel: ToolboxViewModel = ToolboxViewModel(),
    mainActivity: MainActivity? = null
) {
    Column {
        ToolboxAppBar(
            title = stringResource(id = R.string.app_name),
            backgroundColor = viewModel.theme.value.surface,
        )
        Divider()
        LazyVerticalGrid(cells = GridCells.Fixed(2)) {
            item {
                FeatureToggleCard(
                    iconId = R.drawable.ic_light_screen_on,
                    title = "点亮屏幕",
                    modifier = Modifier
                        .padding(16.dp, 16.dp, 8.dp, 8.dp),
                    viewModel = viewModel,
                    isActive = viewModel.lightScreen.value,
                    onClick = onClick@{
                        if (SharedAppData.lightScreen.value!!) {
                            SharedAppData.lightScreen setTo false
                            return@onClick
                        }
                        mainActivity ?: return@onClick
                        if (mainActivity.isIgnoringBatteryOptimizations()) {
                            SharedAppData.lightScreen setTo true
                            return@onClick
                        }
                        mainActivity.checkPermissions(
                            permissionAllGranted = {},
                            onPositiveButtonClick = {
                                mainActivity.startActivity(
                                    Intent(
                                        mainActivity,
                                        PermissionManageActivity::class.java
                                    )
                                )
                            },
                            onNegativeButtonClick = {}
                        )
                    }
                )
            }
            item {
                FeatureToggleCard(
                    iconId = R.drawable.ic_open_player,
                    title = "打开播放器",
                    modifier = Modifier
                        .padding(8.dp, 16.dp, 16.dp, 8.dp),
                    viewModel = viewModel,
                    isActive = viewModel.openPlayer.value,
                    onClick = onClick@{
                        if (SharedAppData.openPlayer.value!!) {
                            SharedAppData.openPlayer setTo false
                            return@onClick
                        }
                        mainActivity ?: return@onClick
                        mainActivity.checkPermissions(
                            permissionAllGranted = {
                                SharedAppData.openPlayer setTo true
                            },
                            onPositiveButtonClick = {
                                mainActivity.startActivity(
                                    Intent(
                                        mainActivity,
                                        PermissionManageActivity::class.java
                                    )
                                )
                            },
                            onNegativeButtonClick = {}
                        )
                    },
                    showSettings = viewModel.openPlayer.value,
                    onSettingsClick = {
                        val intent = Intent(mainActivity, SettingsActivity::class.java)
                        intent.putExtra(
                            "invisibleKeys", arrayOf(
                                PreferenceKeys.CATEGORY_EXPERIMENTAL_FEATURES,
                                PreferenceKeys.CATEGORY_ABOUT,
                                PreferenceKeys.CATEGORY_DEVELOPERS,
                                PreferenceKeys.CATEGORY_OTHER,
                            )
                        )
                        mainActivity?.startActivity(intent)
                    }
                )
            }
            item {
                FeatureToggleCard(
                    imageVector = Icons.Filled.Radar,
                    title = "测试左右声道",
                    isActive = false,
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(16.dp, 8.dp, 8.dp, 8.dp),
                    onClick = {
                        mainActivity?.showChannelTestDialog()
                    },
                    showSettings = true,
                    onSettingsClick = {
                        val intent = Intent(mainActivity, SettingsActivity::class.java)
                        intent.putExtra(
                            "invisibleKeys", arrayOf(
                                PreferenceKeys.CATEGORY_EXPERIMENTAL_FEATURES,
                                PreferenceKeys.CATEGORY_PLAYER_SETTINGS,
                                PreferenceKeys.CATEGORY_ABOUT,
                                PreferenceKeys.CATEGORY_DEVELOPERS,
                                PreferenceKeys.SWITCH_ALLOW_BLUETOOTH
                            )
                        )
                        mainActivity?.startActivity(intent)
                    }
                )
            }
            item {
                FeatureToggleCard(
                    isActive = false,
                    title = "其它偏好设置",
                    imageVector = Icons.Filled.Settings,
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(8.dp, 8.dp, 16.dp, 8.dp),
                    onClick = {
                        val intent = Intent(mainActivity, SettingsActivity::class.java)
                        intent.putExtra(
                            "invisibleKeys", arrayOf(
                                PreferenceKeys.SWITCH_ALLOW_PARALLEL,
                                PreferenceKeys.CATEGORY_PLAYER_SETTINGS
                            )
                        )
                        mainActivity?.startActivity(intent)
                    },
                    showSettings = false
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
@ExperimentalFoundationApi
fun HomePreview() {
    Home()
}