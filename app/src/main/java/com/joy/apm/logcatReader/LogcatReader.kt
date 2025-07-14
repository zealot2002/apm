package com.kongfz.app.monitorrelease.logcat

import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * logcat采集者
 *
 * 外部指定：工作线程、实时logFile
 *
 * LogcatReader.ins.config(executorService,funCurrentLogFile).start()
 *
 * LogcatReader.ins.stop()
 */
class LogcatReader : ILogcatReader {
    private var pid = android.os.Process.myPid()
    private var executorService: ExecutorService? = null
    private var funCurrentLogFile: (() -> File)? = null

    private var currentFileOutputStream: FileOutputStream? = null
    private var logcatProcess: Process? = null
    private var mReader: BufferedReader? = null
    private val cmd = "logcat  | grep \"($pid)\"" //打印所有日志信息

    private var bExit = AtomicBoolean(false)
    private var stopCb: (() -> Unit)? = null

    /*********************************************************************************************/

    companion object {
        //单例 ：debug 和 release不需要同时共存
        val ins by lazy { LogcatReader() }
    }

    override fun config(
        executorService: ExecutorService, getCurrentLogFile: () -> File
    ): ILogcatReader {
        if (this.executorService != null) return this
        this.executorService = executorService
        this.funCurrentLogFile = getCurrentLogFile
        return this
    }

    override fun start() {
        bExit.set(false)
        executorService?.execute {
            doWork()
        }
    }

    override fun stop(cb:(() -> Unit)?) {
        stopCb = cb
        bExit.set(true)
    }

    private fun doWork() {
        try {
            logcatProcess = Runtime.getRuntime().exec(cmd)
            mReader = BufferedReader(InputStreamReader(logcatProcess!!.inputStream), 4096)
            var line: String?
            while (mReader!!.readLine().also { line = it } != null) {
                if (bExit.get()) break
                if (line?.isEmpty() == true) continue
                val currentOutFile = funCurrentLogFile?.invoke()
                currentFileOutputStream = FileOutputStream(currentOutFile, true)
                if (line?.contains(pid.toString()) == true) {
                    //只保存当前进程的log
                    currentFileOutputStream?.write(line!!.toByteArray())
                    currentFileOutputStream?.write("\n".toByteArray())
                }
                currentFileOutputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            logcatProcess?.destroy()
            try {
                mReader?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                currentFileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        stopCb?.invoke()
    }
}


interface ILogcatReader {
    fun start()
    fun stop(cb: (() -> Unit)? = null)
    fun config(executorService: ExecutorService, getCurrentLogFile: () -> File): ILogcatReader
}