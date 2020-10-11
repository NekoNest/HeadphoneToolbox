package com.chheese.app.HeadphoneToolbox.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import androidx.ui.tooling.preview.Preview

class ToolboxActivityCompose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @Preview
    @Composable
    fun testView() {
        Text(text = "Hello")
    }
}