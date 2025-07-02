package com.joy.apm.common

import android.util.Log

class Logger {
    companion object {
        const val TAG = Utils.moduleName
        fun i(s: String, tag: String? = null) {
            Log.i(tag ?: TAG, s)
        }

        fun w(s: String, tag: String? = null) {
            Log.w(tag ?: TAG, s)
        }

        fun e(s: String, tag: String? = null) {
            Log.e(tag ?: TAG, s)
        }
    }
} 