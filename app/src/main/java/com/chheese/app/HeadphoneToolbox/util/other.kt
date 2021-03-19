package com.chheese.app.HeadphoneToolbox.util

import androidx.annotation.RawRes
import com.chheese.app.HeadphoneToolbox.R
import kotlin.random.Random

@RawRes
fun randAudio(right: Boolean = false): Int {
    val audioMap = if (!right) {
        mapOf(
            0 to R.raw.are_you_ok_left,
            1 to R.raw.deep_dark_fantasy_left,
            2 to R.raw.duang_left,
            3 to R.raw.niconiconi_left
        )
    } else {
        mapOf(
            0 to R.raw.are_you_ok_right,
            1 to R.raw.deep_dark_fantasy_right,
            2 to R.raw.duang_right,
            3 to R.raw.niconiconi_right
        )
    }

    val randInt = Random.nextInt(0, 4)
    return audioMap[randInt] ?: error("Index $randInt is not in map.")
}