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

import android.os.Handler
import android.os.HandlerThread
import com.joy.apm.common.Utils

internal class HandlerThreadFactory private constructor() {
    private class HandlerThreadWrapper(threadName: String) {
        var handler: Handler? = null

        init {
            val handlerThread = HandlerThread(Utils.moduleName + "-LooperMonitor-$threadName")
            handlerThread.start()
            handler = Handler(handlerThread.looper)
        }
    }

    companion object {
        private val sLoopThread = HandlerThreadWrapper("loop")
        private val sWriteLogThread = HandlerThreadWrapper("writer")
        val timerThreadHandler = sLoopThread.handler!!
        val writeLogThreadHandler = sWriteLogThread.handler!!
    }

    init {
        throw InstantiationError("Must not instantiate this class")
    }
}