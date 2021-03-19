package com.chheese.app.HeadphoneToolbox.ui

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import com.chheese.app.HeadphoneToolbox.activity.SettingsActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel
import com.chheese.app.HeadphoneToolbox.util.isIgnoringBatteryOptimizations

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
                        .padding(16.dp, 16.dp, 8.dp, 8.dp)
                        .clickable {
                            SharedAppData.lightScreen.value = !SharedAppData.lightScreen.value!!
                            if (SharedAppData.lightScreen.value!! && mainActivity != null) {
                                if (!mainActivity.isIgnoringBatteryOptimizations()) {
                                    mainActivity.requestIgnoreBatteryOptimizations()
                                }
                            }
                        },
                    viewModel = viewModel,
                    isActive = viewModel.lightScreen.value
                )
            }
            item {
                FeatureToggleCard(
                    iconId = R.drawable.ic_open_player,
                    title = "打开播放器",
                    modifier = Modifier
                        .padding(8.dp, 16.dp, 16.dp, 8.dp)
                        .clickable {
                            SharedAppData.openPlayer.value = !SharedAppData.openPlayer.value!!
                            if (SharedAppData.openPlayer.value!! && mainActivity != null) {
                                if (!mainActivity.isIgnoringBatteryOptimizations()) {
                                    mainActivity.requestIgnoreBatteryOptimizations()
                                }
                            }
                        },
                    viewModel = viewModel,
                    isActive = viewModel.openPlayer.value
                )
            }
            item {
                FeatureToggleCard(
                    imageVector = Icons.Filled.Radar,
                    title = "测试左右声道",
                    isActive = false,
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(16.dp, 8.dp, 8.dp, 8.dp)
                        .clickable {
                            mainActivity?.showChannelTestDialog()
                        }
                )
            }
            item {
                FeatureToggleCard(
                    isActive = false,
                    title = "设置",
                    imageVector = Icons.Filled.Settings,
                    viewModel = viewModel,
                    modifier = Modifier
                        .padding(8.dp, 8.dp, 16.dp, 8.dp)
                        .clickable {
                            mainActivity?.startActivity(
                                Intent(mainActivity, SettingsActivity::class.java)
                            )
                        }
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