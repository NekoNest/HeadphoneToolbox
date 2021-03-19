package com.chheese.app.HeadphoneToolbox.data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.chheese.app.HeadphoneToolbox.ui.ShapeCornerSize
import com.chheese.app.HeadphoneToolbox.ui.ShapeType
import com.chheese.app.HeadphoneToolbox.ui.shape
import com.chheese.app.HeadphoneToolbox.ui.theme.ToolboxTheme

class ToolboxViewModel : ViewModel() {
    val lightScreen = mutableStateOf(false)

    val openPlayer = mutableStateOf(false)

    val theme = mutableStateOf(ToolboxTheme.light)

    val shapeType = mutableStateOf(ShapeType.ROUNDED) // cut, none
    val shapeCornerSize = mutableStateOf(ShapeCornerSize(4.dp, 4.dp, 4.dp, 4.dp))
    val shape: MutableState<Shape> = mutableStateOf(shape(shapeType.value, shapeCornerSize.value))
}