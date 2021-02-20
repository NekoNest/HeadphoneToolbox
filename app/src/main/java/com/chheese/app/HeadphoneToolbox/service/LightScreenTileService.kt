package com.chheese.app.HeadphoneToolbox.service

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import androidx.annotation.RequiresApi
import androidx.lifecycle.observe
import com.chheese.app.HeadphoneToolbox.R
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.util.isIgnoringBatteryOptimizations
import com.chheese.app.HeadphoneToolbox.util.logger

@RequiresApi(Build.VERSION_CODES.N)
class LightScreenTileService : LifecycleTileService() {
    override fun onCreate() {
        super.onCreate()
        SharedAppData.lightScreen.observe(this, {
            qsTile.state = if (it) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile.icon = if (it) {
                Icon.createWithResource(this, R.drawable.ic_light_screen_on)
            } else {
                Icon.createWithResource(this, R.drawable.ic_light_screen_off)
            }
            qsTile.updateTile()
        })
    }

    override fun onClick() {
        SharedAppData.lightScreen.value ?: return
        SharedAppData.lightScreen.apply {
            value = !value!!
            if (value!! && !isIgnoringBatteryOptimizations()) {
                requestIgnoreBatteryOptimizations()
            }
            logger.info("用户在通知栏${if (value!!) "打开" else "关闭"}了功能【点亮屏幕】的开关")
        }
    }
}