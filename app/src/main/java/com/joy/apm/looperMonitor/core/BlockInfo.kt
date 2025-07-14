/*
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

import android.os.Build
import android.util.Log
import com.joy.apm.common.Utils
import com.joy.apm.looperMonitor.core.utils.PerformanceUtils
import com.joy.apm.looperMonitor.core.utils.ProcessUtils
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Information to trace a block.
 */
class BlockInfo {
    var qualifier: String? = null
    var model: String? = null
    var apiLevel = ""
    var imei = ""
    var cpuCoreNum = -1

    // Per Block Info fields
    var uid: String? = null
    var processName: String? = null
    var versionName: String? = ""
    var versionCode = 0
    var network: String? = null
    var freeMemory: String? = null
    var totalMemory: String? = null
    var timeCost: Long = 0
    var threadTimeCost: Long = 0
    var timeStart: String? = null
    var timeEnd: String? = null
    var cpuBusy = false
    var cpuRateInfo: String? = null
    var threadStackEntries: ArrayList<String>? = ArrayList()
    private val basicSb = StringBuilder()
    private val cpuSb = StringBuilder()
    private val timeSb = StringBuilder()
    private val stackSb = StringBuilder()

    companion object {
        private const val TAG = "BlockInfo"

        @JvmField
        val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US)
        const val SEPARATOR = "\r\n"
        const val KV = " = "
        const val NEW_INSTANCE_METHOD = "newInstance: "
        const val KEY_QUA = "qua"
        const val KEY_MODEL = "model"
        const val KEY_API = "api-level"
        const val KEY_IMEI = "imei"
        const val KEY_UID = "uid"
        const val KEY_CPU_CORE = "cpu-core"
        const val KEY_CPU_BUSY = "cpu-busy"
        const val KEY_CPU_RATE = "cpu-rate"
        const val KEY_TIME_COST = "time"
        const val KEY_THREAD_TIME_COST = "thread-time"
        const val KEY_TIME_COST_START = "time-start"
        const val KEY_TIME_COST_END = "time-end"
        const val KEY_STACK = "stack"
        const val KEY_PROCESS = "process"
        const val KEY_VERSION_NAME = "versionName"
        const val KEY_VERSION_CODE = "versionCode"
        const val KEY_NETWORK = "network"
        const val KEY_TOTAL_MEMORY = "totalMemory"
        const val KEY_FREE_MEMORY = "freeMemory"
        var sQualifier: String? = null
        var sModel: String? = null
        var sApiLevel = ""

        /**
         * The International Mobile Equipment Identity or IMEI /aɪˈmiː/ is a number,
         * usually unique, to identify 3GPP and iDEN mobile phones
         */
        var sImei = ""
        var sCpuCoreNum = -1
        private const val EMPTY_IMEI = "empty_imei"

        @JvmStatic
        fun newInstance(): BlockInfo {
            val blockInfo = BlockInfo()
            val context = Utils.application
            if (blockInfo.versionName == null || blockInfo.versionName!!.isEmpty()) {
                try {
                    val info = context.packageManager.getPackageInfo(context.packageName, 0)
                    blockInfo.versionCode = info.versionCode
                    blockInfo.versionName = info.versionName
                } catch (e: Throwable) {
                    Log.e(TAG, NEW_INSTANCE_METHOD, e)
                }
            }
            blockInfo.cpuCoreNum = sCpuCoreNum
            blockInfo.model = sModel
            blockInfo.apiLevel = sApiLevel
            blockInfo.qualifier = sQualifier
            blockInfo.imei = sImei
            blockInfo.uid = ""//context.provideUid()
            blockInfo.processName = ProcessUtils.myProcessName(context)
            blockInfo.network = ""//context.provideNetworkType()
            blockInfo.freeMemory = PerformanceUtils.getFreeMemory(context).toString()
            blockInfo.totalMemory = PerformanceUtils.totalMemory.toString()
            return blockInfo
        }

        init {
            sCpuCoreNum = PerformanceUtils.numCores
            sModel = Build.MODEL
            sApiLevel = Build.VERSION.SDK_INT.toString() + " " + Build.VERSION.RELEASE
//            sQualifier = context.provideQualifier()
            try {
//                val telephonyManager = BlockCanaryInternals
//                    .getContext()
//                    .provideContext()
//                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                sImei = EMPTY_IMEI //telephonyManager.getDeviceId();
            } catch (exception: Exception) {
                Log.e(TAG, NEW_INSTANCE_METHOD, exception)
                sImei = EMPTY_IMEI
            }
        }
    }

    fun setCpuBusyFlag(busy: Boolean): BlockInfo {
        cpuBusy = busy
        return this
    }

    fun setRecentCpuRate(info: String?): BlockInfo {
        cpuRateInfo = info
        return this
    }

    fun setThreadStackEntries2(threadStackEntries: ArrayList<String>?): BlockInfo {
        this.threadStackEntries = threadStackEntries
        return this
    }

    fun setMainThreadTimeCost(
        realTimeStart: Long,
        realTimeEnd: Long,
        threadTimeStart: Long,
        threadTimeEnd: Long,
    ): BlockInfo {
        timeCost = realTimeEnd - realTimeStart
        threadTimeCost = threadTimeEnd - threadTimeStart
        timeStart = TIME_FORMATTER.format(realTimeStart)
        timeEnd = TIME_FORMATTER.format(realTimeEnd)
        return this
    }

    fun flushString(): BlockInfo {
        val separator = SEPARATOR
        basicSb.append(KEY_QUA).append(KV).append(qualifier).append(separator)
        basicSb.append(KEY_VERSION_NAME).append(KV).append(versionName).append(separator)
        basicSb.append(KEY_VERSION_CODE).append(KV).append(versionCode).append(separator)
        basicSb.append(KEY_IMEI).append(KV).append(imei).append(separator)
        basicSb.append(KEY_UID).append(KV).append(uid).append(separator)
        basicSb.append(KEY_NETWORK).append(KV).append(network).append(separator)
        basicSb.append(KEY_MODEL).append(KV).append(model).append(separator)
        basicSb.append(KEY_API).append(KV).append(apiLevel).append(separator)
        basicSb.append(KEY_CPU_CORE).append(KV).append(cpuCoreNum).append(separator)
        basicSb.append(KEY_PROCESS).append(KV).append(processName).append(separator)
        basicSb.append(KEY_FREE_MEMORY).append(KV).append(freeMemory).append(separator)
        basicSb.append(KEY_TOTAL_MEMORY).append(KV).append(totalMemory).append(separator)
        timeSb.append(KEY_TIME_COST).append(KV).append(timeCost).append(separator)
        timeSb.append(KEY_THREAD_TIME_COST).append(KV).append(threadTimeCost).append(separator)
        timeSb.append(KEY_TIME_COST_START).append(KV).append(timeStart).append(separator)
        timeSb.append(KEY_TIME_COST_END).append(KV).append(timeEnd).append(separator)
        cpuSb.append(KEY_CPU_BUSY).append(KV).append(cpuBusy).append(separator)
        cpuSb.append(KEY_CPU_RATE).append(KV).append(cpuRateInfo).append(separator)
        if (threadStackEntries != null && !threadStackEntries!!.isEmpty()) {
            val temp = StringBuilder()
            for (s in threadStackEntries!!) {
                temp.append(s)
                temp.append(separator)
            }
            stackSb.append(KEY_STACK).append(KV).append(temp.toString()).append(separator)
        }
        return this
    }

    val basicString: String
        get() = basicSb.toString()
    val cpuString: String
        get() = cpuSb.toString()
    val timeString: String
        get() = timeSb.toString()

    override fun toString(): String {
        return basicSb.toString() + timeSb + cpuSb + stackSb
    }
}