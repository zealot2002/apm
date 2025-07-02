package com.joy.apm.common

import android.os.Handler
import android.os.Looper
import android.widget.Toast

class Toaster {
    companion object {
        private val handler = Handler(Looper.getMainLooper())
        fun show(s: String) {
            handler.post {
                Toast.makeText(Utils.application, s, Toast.LENGTH_LONG).show()
            }
        }
    }
} 