/*
 * Copyright (C) 2016 MarkZhai (http://zhaiyifan.cn).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joy.apm.looperMonitor.core.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.FileFilter
import java.io.FileReader
import java.io.IOException
import java.util.regex.Pattern

internal class PerformanceUtils private constructor() {
    companion object {
        private const val TAG = "PerformanceUtils"
        private var sCoreNum = 0
        private var sTotalMemo: Long = 0// Get directory containing CPU info
        // Filter to only list the devices we care about
        // Return the number of cores (virtual CPU devices)
        /**
         * Get cpu core number
         *
         * @return int cpu core number
         */
        val numCores: Int
            get() {
                class CpuFilter : FileFilter {
                    override fun accept(pathname: File): Boolean {
                        return Pattern.matches("cpu[0-9]", pathname.name)
                    }
                }
                if (sCoreNum == 0) {
                    sCoreNum = try {
                        // Get directory containing CPU info
                        val dir = File("/sys/devices/system/cpu/")
                        // Filter to only list the devices we care about
                        val files = dir.listFiles(CpuFilter())
                        // Return the number of cores (virtual CPU devices)
                        files!!.size
                    } catch (e: Exception) {
                        Log.e(TAG, "getNumCores exception", e)
                        1
                    }
                }
                return sCoreNum
            }

        fun getFreeMemory(context: Context): Long {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            return mi.availMem / 1024
        }

        val totalMemory: Long
            get() {
                if (sTotalMemo == 0L) {
                    val str1 = "/proc/meminfo"
                    val str2: String
                    val arrayOfString: Array<String>
                    var initial_memory: Long = -1
                    var localFileReader: FileReader? = null
                    try {
                        localFileReader = FileReader(str1)
                        val localBufferedReader = BufferedReader(localFileReader, 8192)
                        str2 = localBufferedReader.readLine()
                        if (str2 != null) {
                            initial_memory = str2.filter {
                                it in '0'..'9'
                            }.toLong()
                        }
                        localBufferedReader.close()
                    } catch (e: IOException) {
                        Log.e(TAG, "getTotalMemory exception = ", e)
                    } finally {
                        if (localFileReader != null) {
                            try {
                                localFileReader.close()
                            } catch (e: IOException) {
                                Log.e(TAG, "close localFileReader exception = ", e)
                            }
                        }
                    }
                    sTotalMemo = initial_memory
                }
                return sTotalMemo
            }
    }

    init {
        throw InstantiationError("Must not instantiate this class")
    }
}