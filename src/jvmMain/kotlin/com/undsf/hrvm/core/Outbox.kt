package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException
import com.undsf.hrvm.core.exceptions.WrongAnswerException

class Outbox() : DataQueue() {
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
    }

    override fun pop(): Data? {
        throw RuntimeException("无法从输出队列获取数据")
    }

    override fun push(data: Data) {
        super.push(data)
        if (size > samples.size) {
            throw WrongAnswerException(Data(size), Data(samples.size), "输出长度与预期不符")
        }

        val expected = samples[size - 1]
        if (expected != data) {
            throw WrongAnswerException(expected, data, "第${size}个结果与预期不符")
        }
    }

    override fun reset() {
        datas.clear()
    }
}