package com.joy.apm

/**
 * 工具开关
 */
class KitSwitch {
    companion object {
        /**
         * 总开关
         */
        var enable = true

        /**
         * StrictMode
         */
        var bStrictMode = true

        /**
         * logcat采集，每一分钟产生一个日志文件，帮助定位非必现bug
         */
        var bLogcatReader = true

        /**
         * 主线程looper监控
         *
         * 1，实时打印looper每一个event的处理日志 （异步的死循环很容易被检测到）
         *
         * 2，当主线程looper中的某一个event执行时间超过了阈值（默认300ms），会产生block日志文件
         */
        var bLooperMonitor = true

        /**
         * 线程采样
         *
         * 在logcat中查看、不会产生日志文件
         *
         * 实时监控线程的创建行为、销毁行为，监控线程数量
         *
         * 每隔15秒list一次
         */
        var bThreadSampler = true
    }
} 