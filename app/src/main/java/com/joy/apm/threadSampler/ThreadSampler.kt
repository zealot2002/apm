package com.joy.apm.threadSampler

import com.joy.apm.common.AbstractSampler
import com.joy.apm.common.Logger
import com.joy.apm.common.Utils
import java.util.TreeSet

/**
 * 线程采样，实时监控线程的创建、销毁
 */
class ThreadSampler : AbstractSampler(Cfg.threadSamplerInterval) {

    private var old = TreeSet<String>()
    private var count = 1
    private val tag = Utils.moduleName + "-ThreadSampler"

    /**************************************************************************************************/
    override fun doSample() {

        val count = Thread.activeCount()
        val arr = arrayOfNulls<Thread>(count)

        Logger.e("thread activeCount :$count", tag)
        Thread.enumerate(arr)
        doLogging(arr)
    }

    private fun doLogging(arr: Array<Thread?>) {
        if (old.isEmpty()) {
            for (t in arr) {
                t?.let {
                    Logger.w("激活: " + it.name, tag)
                    old.add(it.name)
                }
            }
        } else {
            val new = HashSet<String>()
            for (t in arr) {
                t?.let {
                    if (!old.contains(it.name)) {
                        Logger.w("激活: " + it.name, tag)
                        old.add(it.name)
                    }
                    new.add(it.name)
                }
            }
            val iterator = old.iterator()
            while (iterator.hasNext()) {
                val name = iterator.next()
                if (!new.contains(name)) {
                    Logger.w("挂起/回收: $name", tag)
                    iterator.remove()
                }
            }
        }

        if (count++ == Cfg.threadListCondition) {
            count = 1
            Logger.e("-------------- 活跃线程 start --------------", tag)
            for (n in old) Logger.w(n, tag)
            Logger.e("-------------- 活跃线程 end --------------", tag)
        }
    }
} 