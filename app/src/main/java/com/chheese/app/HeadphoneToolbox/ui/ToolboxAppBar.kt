package com.chheese.app.HeadphoneToolbox.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToolboxAppBar(
    title: String,
    backgroundColor: Color,
    showBackIcon: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .background(backgroundColor)
    ) {
        Surface {
            if (showBackIcon) {
                Box(
                    modifier = Modifier
                        .clickable {
                            onBackClick()
                        }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "返回上级",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = title,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
fun ToolboxAppBarPreview() {
    ToolboxAppBar(
        title = "测试",
        backgroundColor = Color.White,
        showBackIcon = true
    )
}