package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

open class DataQueue() {
    val datas = ArrayList<Data>()

    val size get() = datas.size

    constructor(vararg ds: Any) : this() {
        load(ds.toList())
    }

    constructor(ds: List<Any>) : this() {
        load(ds)
    }

    open fun load(ds: List<Any>) {
        for (d in ds) {
            when (d) {
                is Char -> datas.add(Data(d))
                is Int -> datas.add(Data(d))
                is Data -> datas.add(d.clone())
                else -> throw RuntimeException("无效的数据类型：$d")
            }
        }
    }

    open fun push(data: Data) {
        datas.add(data.clone())
    }

    open fun pop() : Data? {
        if (datas.size <= 0) {
            return null
        }
        return datas.removeAt(0)
    }

    open fun reset() {
        datas.clear()
    }

    operator fun get(index: Int) : Data {
        return datas[index]
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[ ")
        var first = true
        for (d in datas) {
            if (first) {
                first = false
            }
            else {
                builder.append(", ")
            }
            builder.append(d)
        }
        builder.append(" ]")
        return builder.toString()
    }
}