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

import com.joy.apm.common.AbstractSampler
import com.joy.apm.looperMonitor.core.BlockInfo


/**
 * Dumps thread stack.
 */
class StackSampler(
    private val mCurrentThread: Thread,
    private val mMaxEntryCount: Int,
    sampleIntervalMillis: Long,
) : AbstractSampler(sampleIntervalMillis) {


    companion object {
        private const val DEFAULT_MAX_ENTRY_COUNT = 100
        private val sStackMap = LinkedHashMap<Long, String>()
    }

    /**************************************************************************************************/
    constructor(thread: Thread) : this(
        thread,
        DEFAULT_MAX_ENTRY_COUNT,
        300
    )

    fun getThreadStackEntries(startTime: Long, endTime: Long): ArrayList<String> {
        val result = ArrayList<String>()
        synchronized(sStackMap) {
            for (entryTime in sStackMap.keys) {
                if (entryTime in (startTime + 1) until endTime) {
                    result.add(
                        BlockInfo.Companion.TIME_FORMATTER.format(entryTime)
                                + BlockInfo.Companion.SEPARATOR
                                + BlockInfo.Companion.SEPARATOR
                                + sStackMap[entryTime]
                    )
                }
            }
        }
        return result
    }

    //less than 3ms
    override fun doSample() {
        val stringBuilder = StringBuilder()
        for (stackTraceElement in mCurrentThread.stackTrace) {
            stringBuilder
                .append(stackTraceElement.toString())
                .append(BlockInfo.Companion.SEPARATOR)
        }
        synchronized(sStackMap) {
            if (sStackMap.size == mMaxEntryCount && mMaxEntryCount > 0) {
                sStackMap.remove(sStackMap.keys.iterator().next())
            }
            sStackMap.put(System.currentTimeMillis(), stringBuilder.toString())
        }
    }

}