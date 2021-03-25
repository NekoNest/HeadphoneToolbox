package com.chheese.app.HeadphoneToolbox.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.chheese.app.HeadphoneToolbox.ui.components.ShapeCornerSize
import com.chheese.app.HeadphoneToolbox.ui.components.ShapeType
import com.chheese.app.HeadphoneToolbox.ui.components.shape
import com.chheese.app.HeadphoneToolbox.ui.theme.ToolboxTheme

class ToolboxViewModel : ViewModel() {
    val lightScreen = mutableStateOf(false)

    val openPlayer = mutableStateOf(false)

    val theme = mutableStateOf(ToolboxTheme.light)

    val shapeType = mutableStateOf(ShapeType.ROUNDED) // cut, none

    val previewActive = mutableStateOf(false)

    val topStartCornerSize = mutableStateOf(0)
    val topEndCornerSize = mutableStateOf(0)
    val bottomStartCornerSize = mutableStateOf(0)
    val bottomEndCornerSize = mutableStateOf(0)

    val colorTextFieldValue = mutableStateOf("#000000")
    val colorTextFieldError = mutableStateOf(false)

    val shapeCornerSize = mutableStateOf(
        ShapeCornerSize(
            topStartCornerSize.value.dp,
            topEndCornerSize.value.dp,
            bottomStartCornerSize.value.dp,
            bottomEndCornerSize.value.dp
        )
    )
    val shape: MutableState<Shape> = mutableStateOf(shape(shapeType.value, shapeCornerSize.value))

    val isIgnoreBatteryOptimization = mutableStateOf(false)
    val isAllowSystemDialog = mutableStateOf(false)
}