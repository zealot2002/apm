package com.joy.apm.strictMode

import android.os.StrictMode


class StrictModeHelper {
    companion object {
        fun init() {
            //开启Thread策略模式
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectNetwork() //监测主线程使用网络io
                    .detectCustomSlowCalls() //监测自定义运行缓慢函数
                    .detectDiskReads() // 检测在UI线程读磁盘操作
                    .detectDiskWrites() // 检测在UI线程写磁盘操作
                    .penaltyLog() //写入日志
//                    .penaltyDialog() //监测到上述状况时弹出对话框
                    .build()
            )
            //开启VM策略模式
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects() //监测sqlite泄露
                    .detectLeakedClosableObjects() //监测没有关闭IO对象
                    .detectActivityLeaks()
                    .penaltyLog() //写入日志
//                    .penaltyDeath() //出现上述情况异常终止
                    .build()
            )
        }
    }
}