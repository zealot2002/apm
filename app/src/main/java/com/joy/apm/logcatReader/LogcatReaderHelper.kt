package com.joy.apm.logcatReader

import com.joy.apm.common.FileUtils
import com.joy.apm.common.Utils
import com.kongfz.app.monitorrelease.logcat.LogcatReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class LogcatReaderHelper {

    companion object {
        fun init() {
            FileUtils.delOldFiles(Cfg.kitDirPath)

            val executorService: ExecutorService = Executors.newSingleThreadExecutor { r ->
                val thread = Thread(r)
                thread.name = Utils.moduleName + "-LogcatReader"
                thread
            }
            LogcatReader.ins.config(executorService) {
                FileUtils.getLogFile(Cfg.kitDirPath)
            }.start()
        }
    }
}