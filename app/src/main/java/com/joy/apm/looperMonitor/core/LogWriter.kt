/*
 * Copyright (C) 2016 MarkZhai (http://zhaiyifan.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joy.apm.looperMonitor.core

import android.util.Log
import com.joy.apm.common.FileUtils
import com.joy.apm.logcatReader.Cfg
import com.kongfz.app.monitor.looperMonitor.core.BlockInfo
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Log writer which runs in standalone thread.
 */
class LogWriter private constructor() {
    companion object {
        private val TAG = "LogWriter"
        private val SAVE_DELETE_LOCK = Any()
        private val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

        fun save(str: String) {
            synchronized(SAVE_DELETE_LOCK) {
                var writer: BufferedWriter? = null
                try {
                    val time = System.currentTimeMillis()
                    val logFile = FileUtils.getLogFile(
                        Cfg.kitDirPath,
                        FileUtils.getTodayString() + System.currentTimeMillis().toString()
                    )
                    val out =
                        OutputStreamWriter(FileOutputStream(logFile.path, true), "UTF-8")
                    writer = BufferedWriter(out)
                    writer.write(BlockInfo.Companion.SEPARATOR)
                    writer.write("**********************")
                    writer.write(BlockInfo.Companion.SEPARATOR)
                    writer.write(TIME_FORMATTER.format(time) + "(write log time)")
                    writer.write(BlockInfo.Companion.SEPARATOR)
                    writer.write(BlockInfo.Companion.SEPARATOR)
                    writer.write(str)
                    writer.write(BlockInfo.Companion.SEPARATOR)
                    writer.flush()
                    writer.close()
                    writer = null
                } catch (t: Throwable) {
                    Log.e(TAG, "save: ", t)
                } finally {
                    try {
                        writer?.close()
                    } catch (e: Exception) {
                        Log.e(TAG, "save: ", e)
                    }
                }
            }
        }
    }

    init {
        throw InstantiationError("Must not instantiate this class")
    }
}