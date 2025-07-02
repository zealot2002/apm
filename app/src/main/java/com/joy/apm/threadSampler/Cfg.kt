package com.joy.apm.threadSampler

object Cfg {
    const val threadSamplerInterval: Long = 5000 //线程监控轮询间隔
    const val threadListCondition: Int = 10  //list条件：每10次采样list一次
} 