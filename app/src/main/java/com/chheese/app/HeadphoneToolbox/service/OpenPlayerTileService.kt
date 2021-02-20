package com.chheese.app.HeadphoneToolbox.service

import android.os.Build
import android.service.quicksettings.Tile
import androidx.annotation.RequiresApi
import androidx.lifecycle.observe
import com.chheese.app.HeadphoneToolbox.data.SharedAppData
import com.chheese.app.HeadphoneToolbox.util.isIgnoringBatteryOptimizations
import com.chheese.app.HeadphoneToolbox.util.logger

@RequiresApi(Build.VERSION_CODES.N)
class OpenPlayerTileService : LifecycleTileService() {
    override fun onCreate() {
        super.onCreate()
        SharedAppData.openPlayer.observe(this, {
            qsTile.state = if (it) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            qsTile.updateTile()
        })
    }

    override fun onClick() {
        SharedAppData.openPlayer.value ?: return
        SharedAppData.openPlayer.apply {
            value = !value!!
            if (value!! && !isIgnoringBatteryOptimizations()) {
                requestIgnoreBatteryOptimizations()
            }
            logger.info("用户在通知栏${if (value!!) "打开" else "关闭"}了功能【打开播放器】的开关")
        }
    }
}