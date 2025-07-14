package com.joy.apm.looperMonitor

import com.kongfz.app.monitor.common.FileUtils
import com.kongfz.app.monitor.common.Toaster
import com.kongfz.app.monitor.common.Utils
import com.kongfz.app.monitor.logcatReader.Cfg
import com.kongfz.app.monitor.looperMonitor.core.LooperMonitor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object LooperMonitorHelper {
    val executor = ThreadPoolExecutor(
        1,
        1,
        0L,
        TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(),
        MyThreadFactory()
    )

    fun init() {
        FileUtils.delOldFiles(Cfg.kitDirPath)
        executor.execute {
            LooperMonitor()
        }
    }
}

class MyThreadFactory : ThreadFactory {
    override fun newThread(r: Runnable?): Thread {
        val t = object : Thread(r, Utils.moduleName + "-LooperMonitor-daemon") {
            override fun run() {
                try {
                    super.run()
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Toaster.show(e.toString())
                }
            }
        }
        t.isDaemon = true
        t.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, e -> println(e) }
        t.priority = Thread.MIN_PRIORITY
        return t
    }
}