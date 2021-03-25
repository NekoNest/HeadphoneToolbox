package com.chheese.app.HeadphoneToolbox.data

import android.app.Activity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.chheese.app.HeadphoneToolbox.ui.components.ShapeCornerSize
import com.chheese.app.HeadphoneToolbox.ui.components.ShapeType
import com.chheese.app.HeadphoneToolbox.ui.components.shape

object SharedAppData {
    val lightScreen = MutableLiveData(false)
    val openPlayer = MutableLiveData(false)
    val isServiceRunning = MutableLiveData(false)
    val topActivity = MutableLiveData<Activity>() // 用于记录自己的Activity是否处于顶层

    val shapeType = MutableLiveData("round")
    val topStartCornerSize = MutableLiveData(0)
    val topEndCornerSize = MutableLiveData(0)
    val bottomStartCornerSize = MutableLiveData(0)
    val bottomEndCornerSize = MutableLiveData(0)

    val colorPrimary = MutableLiveData("0xFF6200EE")

    val shapeCornerSize = MutableLiveData(
        ShapeCornerSize(
            topStartCornerSize.value!!.dp,
            topEndCornerSize.value!!.dp,
            bottomStartCornerSize.value!!.dp,
            bottomEndCornerSize.value!!.dp
        )
    )

    val shape = MutableLiveData(
        shape(
            ShapeType.ROUNDED,
            shapeCornerSize.value!!,
        )
    )
}