package com.chheese.app.HeadphoneToolbox.data

import android.app.Activity
import androidx.lifecycle.MutableLiveData

object SharedAppData {
    val lightScreen = MutableLiveData(false)
    val openPlayer = MutableLiveData(false)
    val isServiceRunning = MutableLiveData(false)
    val topActivity = MutableLiveData<Activity>() // 用于记录自己的Activity是否处于顶层
}