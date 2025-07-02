package com.joy.apm.common

import android.annotation.SuppressLint
import android.app.Application

class Utils {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var application: Application

        const val moduleName = "joyApm"
    }
} 