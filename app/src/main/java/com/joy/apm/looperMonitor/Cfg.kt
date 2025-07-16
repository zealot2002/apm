package com.joy.apm.looperMonitor

object Cfg {
    const val kitDirPath = "block"
    const val blockThreshold = 300L
    val whiteList = mutableListof(
        //第三方
        "com.alibaba.security.common.track.impl.RPTrackManager",  //once/5s
        "com.tencent.bugly.proguard",  //once/2s
        "com.networkbench.agent.impl",  //听云
        "leakcanary.",
        //系统
        "android.widget.Editor",
        "android.view.Choreographer",  //once/16ms

        "io.reactivex.rxjava3.android.schedulers.HandlerScheduler"
    )
}