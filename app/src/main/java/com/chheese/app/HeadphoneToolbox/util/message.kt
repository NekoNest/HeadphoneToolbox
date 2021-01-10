package com.chheese.app.HeadphoneToolbox.util

import android.os.Message

fun newMessage(what: Int, arg1: Int = -1): Message {
    val message = Message.obtain()
    message.what = what
    if (arg1 != -1) {
        message.arg1 = arg1
    }
    return message
}