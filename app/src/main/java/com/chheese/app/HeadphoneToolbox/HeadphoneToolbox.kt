package com.chheese.app.HeadphoneToolbox

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import moe.lemonneko.logger.NekoLogger
import moe.lemonneko.logger.appender.SizeRollingAppender
import moe.lemonneko.logger.appender.TerminalAppender
import moe.lemonneko.logger.util.Constants.Size.MB
import moe.lemonneko.nekologger.ktx.appender
import moe.lemonneko.nekologger.ktx.appenders
import moe.lemonneko.nekologger.ktx.applyConfig
import org.slf4j.LoggerFactory

class HeadphoneToolbox : Application() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var logger: NekoLogger
    override fun onCreate() {
        super.onCreate()
        logger = LoggerFactory.getLogger(this::class.java) as NekoLogger

        logger.applyConfig {
            appenders {
                appender<SizeRollingAppender> {
                    maxSize = 2 * MB
                    targetDirectory = filesDir
                }
                appender<TerminalAppender> {
                    format = "%SIMPLE_CLASS_NAME.%METHOD_NAME: %MESSAGE"
                }
            }
        }

        logger.info("新建Application对象，日志打印工具初始化完成")

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        logger.info("数据存储器初始化完成")
    }
}