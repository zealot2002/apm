/*
 */
package com.joy.apm.looperMonitor.core

import android.os.Looper
import android.os.SystemClock
import android.util.Printer
import com.joy.apm.common.Logger
import com.joy.apm.common.Toaster
import com.joy.apm.looperMonitor.Cfg
import com.joy.apm.looperMonitor.LooperMonitorHelper

internal class LooperMonitor : Printer {
    private var mStartTimestamp = 0L
    private var mStartThreadTimestamp = 0L

    private val stackSampler = StackSampler(Looper.getMainLooper().thread)

    init {
        Looper.getMainLooper().setMessageLogging(this)
    }

    /*****************************************************************************************************/
    override fun println(s: String) {
        doInBackground(s)
    }

    private fun doInBackground(s: String) {
        LooperMonitorHelper.executor.execute {
            if (!match(Cfg.whiteList, s)) {
                Logger.i(s)
            }
            if (s.startsWith('>')) {
                mStartTimestamp = System.currentTimeMillis()
                mStartThreadTimestamp = SystemClock.currentThreadTimeMillis()
                stackSampler.start()
            } else {
                val endTime = System.currentTimeMillis()
                if (isBlock(endTime)) {
                    val error =
                        "block occurred! cost :" + (endTime - mStartTimestamp).toString() + "ms "
                    Toaster.show(error)
                    Logger.e(error + s)
                    doDump(endTime)
                }
                stackSampler.stop()
            }
        }
    }

    private fun doDump(endTime: Long) {
        val startTime = mStartTimestamp
        val startThreadTime = mStartThreadTimestamp
        val endThreadTime = SystemClock.currentThreadTimeMillis()
        // Get recent thread-stack entries and cpu usage
        val threadStackEntries: ArrayList<String> =
            stackSampler.getThreadStackEntries(startTime, endTime)
        if (threadStackEntries.isNotEmpty()) {
            val blockInfo: BlockInfo = BlockInfo.newInstance()
                .setMainThreadTimeCost(
                    startTime,
                    endTime,
                    startThreadTime,
                    endThreadTime
                )
//                .setCpuBusyFlag(cpuSampler.isCpuBusy(realTimeStart, realTimeEnd))
//                .setRecentCpuRate(cpuSampler.getCpuRateInfo())
                .setThreadStackEntries2(threadStackEntries)
                .flushString()
            LogWriter.save(blockInfo.toString())
//            if (mInterceptorChain.size != 0) {
//                for (interceptor in mInterceptorChain) {
//                    interceptor.onBlock(BlockCanaryInternals.getContext().provideContext(),
//                        blockInfo)
//                }
//            }
        }
    }

    private fun isBlock(endTime: Long): Boolean {
        return (endTime - mStartTimestamp) >= Cfg.blockThreshold
    }

    private fun match(ignoreList: MutableList<String>, str: String): Boolean {
        for (s in ignoreList) {
            if (str.contains(s))
                return true
        }
        return false
    }
}
