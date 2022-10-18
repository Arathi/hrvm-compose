package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

class Inbox() : DataQueue() {
    val samples = mutableListOf<Data>()

    constructor(vararg ds : Any) : this() {
        load(ds.toList())
    }

    constructor(ds: List<Any>) : this() {
        load(ds)
    }

    override fun load(ds: List<Any>) {
        for (d in ds) {
            when(d) {
                is Int -> samples.add(Data(d))
                is Char -> samples.add(Data(d))
                is Data -> samples.add(d.clone())
                else -> throw RuntimeException("无效的数据类型：$d")
            }
        }
        reset()
    }

    override fun push(data: Data) {
        throw RuntimeException("无法向输入队列推送数据")
    }

    override fun reset() {
        datas.clear()
        for (s in samples) {
            datas.add(s.clone())
        }
    }
}