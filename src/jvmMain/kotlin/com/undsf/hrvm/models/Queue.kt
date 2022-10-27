package com.undsf.hrvm.models

import androidx.compose.runtime.mutableStateListOf
import com.undsf.hrvm.exceptions.MachineBuildException

class Queue(val sampleDatas: List<Any>, val type: QueueType = QueueType.Inbox) {
    val datas = mutableStateListOf<Data>()
    val size: Int get() = datas.size

    init {
        reset()
    }

    fun reset() {
        datas.clear()
        if (type == QueueType.Inbox) {
            for (sd in sampleDatas) {
                val data = when (sd) {
                    is Int -> Data(sd)
                    is Char -> Data(sd)
                    is Data -> sd.clone()
                    else -> throw MachineBuildException("队列数据初始化异常")
                }
                push(data)
            }
        }
    }

    fun push(data: Data) {
        datas.add(data)
    }

    fun push(v: Int) {
        push(Data(v))
    }

    fun push(ch: Char) {
        push(Data(ch))
    }

    fun pop(): Data? {
        if (datas.isEmpty()) {
            return null
        }
        return datas.removeFirst()
    }
}