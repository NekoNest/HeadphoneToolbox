package com.chheese.app.HeadphoneToolbox.util

import androidx.lifecycle.MutableLiveData

infix fun <T> MutableLiveData<T>.setTo(t: T?) {
    this.value = t
}