package com.chheese.app.HeadphoneToolbox.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Screenshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chheese.app.HeadphoneToolbox.activity.UiSettingsActivity
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.data.ToolboxViewModel
import com.chheese.app.HeadphoneToolbox.ui.components.FeatureToggleCard
import com.chheese.app.HeadphoneToolbox.ui.components.ToolboxAppBar
import java.util.*

@Composable
fun UiSettings(
    viewModel: ToolboxViewModel,
    activity: UiSettingsActivity? = null
) {
    Column {
        ToolboxAppBar(
            title = "新版用户界面自定义设置",
            backgroundColor = viewModel.theme.value.background,
            showBackIcon = true,
            onBackClick = {
                activity?.finish()
            }
        )
        Divider()
        FeatureToggleCard(
            imageVector = Icons.Filled.Screenshot,
            isActive = viewModel.previewActive.value,
            title = "预览",
            viewModel = viewModel,
            modifier = Modifier
                .padding(8.dp),
            onClick = {
                viewModel.previewActive.value = !viewModel.previewActive.value
            },
            showSettings = viewModel.previewActive.value
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp, 8.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("边角类型：")
            val options = arrayOf(
                "无",
                "圆角",
                "钻石切边"
            )
            RadioGroup(
                options = options,
                onValueChange = {
                    SharedAppData.shapeType.value = when (it) {
                        options[0] -> "none"
                        options[1] -> "round"
                        options[2] -> "cut"
                        else -> error("没有这种边角类型")
                    }
                },
                orientation = Orientation.Horizontal,
                selected = viewModel.shapeType.value.zhName
            )
        }
        SliderWithText(
            title = "左上角半径",
            modifier = Modifier
                .padding(8.dp, 0.dp, 8.dp, 8.dp),
            onValueChange = {
                SharedAppData.topStartCornerSize.value = it.toInt()
            },
            value = viewModel.topStartCornerSize.value
        )
        SliderWithText(
            title = "右上角半径",
            modifier = Modifier
                .padding(8.dp, 0.dp, 8.dp, 8.dp),
            onValueChange = {
                SharedAppData.topEndCornerSize.value = it.toInt()
            },
            value = viewModel.topEndCornerSize.value
        )
        SliderWithText(
            title = "左下角半径",
            modifier = Modifier
                .padding(8.dp, 0.dp, 8.dp, 8.dp),
            value = viewModel.bottomStartCornerSize.value,
            onValueChange = {
                SharedAppData.bottomStartCornerSize.value = it.toInt()
            },
        )
        SliderWithText(
            title = "右下角半径",
            modifier = Modifier
                .padding(8.dp, 0.dp, 8.dp, 8.dp),
            value = viewModel.bottomEndCornerSize.value,
            onValueChange = {
                SharedAppData.bottomEndCornerSize.value = it.toInt()
            },
        )
        OutlinedTextField(
            value = viewModel.colorTextFieldValue.value,
            onValueChange = {
                viewModel.colorTextFieldValue.value = it
                val regex = "#[0-9a-fA-F]{6}".toRegex()
                if (it.matches(regex)) {
                    SharedAppData.colorPrimary.value = (
                            it.substring(1)).toUpperCase(Locale.ROOT)
                    viewModel.colorTextFieldError.value = false
                } else {
                    viewModel.colorTextFieldError.value = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            label = {
                Text(
                    text = if (viewModel.colorTextFieldError.value) {
                        "主色调十六进制颜色值（格式错误）"
                    } else {
                        "主色调十六进制颜色值"
                    }
                )
            },
            isError = viewModel.colorTextFieldError.value
        )
    }
}

@Composable
fun SliderWithText(
    modifier: Modifier = Modifier,
    title: String,
    onValueChange: (Float) -> Unit = {},
    value: Int = 0
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = title,
            modifier = Modifier
                .padding(end = 8.dp)
        )
        Slider(
            value = value.toFloat(),
            onValueChange = {
                onValueChange(it)
            },
            steps = 20,
            valueRange = 0f..20f
        )
    }
}

@Composable
fun RadioGroup(
    modifier: Modifier = Modifier,
    options: Array<String>,
    onValueChange: (String) -> Unit,
    orientation: Orientation = Orientation.Vertical,
    selected: String = ""
) {
    if (orientation == Orientation.Vertical) {
        Column(
            modifier = modifier
        ) {
            RadioGroupInternal(
                options = options,
                onValueChange = onValueChange,
                selected = selected
            )
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            RadioGroupInternal(
                options = options,
                onValueChange = onValueChange,
                selected = selected
            )
        }
    }
}

@Composable
private fun RadioGroupInternal(
    options: Array<String>,
    onValueChange: (String) -> Unit,
    selected: String
) {
    for (option in options) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onValueChange(option)
            }
        ) {
            RadioButton(
                selected = option == selected,
                onClick = {
                    onValueChange(option)
                })
            Text(
                text = option
            )
        }
    }
}

fun Color(colorStr: String): Color {
    val color = Color(colorStr.toInt(16))
    return Color(color.red, color.green, color.blue)
}

@Preview(showBackground = true)
@Composable
fun SliderWithTextPreview() {
    SliderWithText(
        title = "圆角半径（单位：DP）"
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UiSettingsPreview() {
    UiSettings(viewModel = ToolboxViewModel())
}