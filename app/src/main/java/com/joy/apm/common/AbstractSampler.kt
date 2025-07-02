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
package com.joy.apm.common

import com.joy.apm.looperMonitor.core.HandlerThreadFactory
import java.util.concurrent.atomic.AtomicBoolean

/**
 * [AbstractSampler] sampler defines sampler work flow.
 */
abstract class AbstractSampler(private val sampleInterval: Long) {
    protected var mShouldSample = AtomicBoolean(false)

    @JvmField
    protected var mSampleInterval: Long = 0
    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            doSample()
            if (mShouldSample.get()) {
                HandlerThreadFactory.timerThreadHandler
                    .postDelayed(this, mSampleInterval)
            }
        }
    }

    open fun start() {
        if (mShouldSample.get()) {
            return
        }
        mShouldSample.set(true)
        HandlerThreadFactory.timerThreadHandler.removeCallbacks(mRunnable)
        HandlerThreadFactory.timerThreadHandler.postDelayed(
            mRunnable,
            (sampleInterval * 0.9).toLong()
        )
    }

    fun stop() {
        if (!mShouldSample.get()) {
            return
        }
        mShouldSample.set(false)
        HandlerThreadFactory.timerThreadHandler.removeCallbacks(mRunnable)
    }

    abstract fun doSample()

    init {
        mSampleInterval =
            if (0L == sampleInterval) {
                300L
            } else {
                sampleInterval
            }
    }
} 