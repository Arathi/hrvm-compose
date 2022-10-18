package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

class Memory(var width: Int = 1, var height: Int = 1) {
    val size: Int
        get() = width * height

    val datas = mutableListOf<Data?>()
    val nameToIndexMap = mutableMapOf<String, Int>()
    val indexToNameMap = mutableMapOf<Int, String>()

    init {
        reset()
    }

    constructor(width: Int, height: Int, vararg data: Any?) : this(width, height) {
        load(*data)
    }

    fun load(vararg ds: Any?) {
        for (i in ds.indices) {
            val d = ds[i]
            when (d) {
                is Char -> datas[i] = Data(d)
                is Int -> datas[i] = Data(d)
                is Data -> datas[i] = d
                null -> datas[i] = null
                else -> throw RuntimeException("无效的数据类型：$d")
            }
        }
    }

    fun reset() {
        datas.clear()
        for (i in 0 until size) {
            datas.add(null)
        }
    }

    fun register(name: String, index: Int) {
        nameToIndexMap[name] = index
        indexToNameMap[index] = name
    }

    fun getIndex(x: Int, y: Int) : Int {
        if (x < 0 || x >= width) {
            throw RuntimeException("无效的x坐标：$x")
        }
        if (y < 0 || y >= height) {
            throw RuntimeException("无效的y坐标：$y")
        }
        return x + y * width
    }

    fun getIndex(name: String) : Int {
        var index: Int? = null
        if (nameToIndexMap.containsKey(name)) {
            index = nameToIndexMap[name]
        }
        if (index == null) {
            throw RuntimeException("未找到名为${name}的变量")
        }
        return index
    }

    fun getData(index: Int) : Data? {
        if (index < 0 || index >= size) {
            throw RuntimeException("无效的下标：$index")
        }
        return datas[index]
    }

    operator fun get(index: Int) : Data {
        if (index < 0 || index >= size) {
            throw RuntimeException("无效的下标：$index")
        }
        val data = datas[index] ?: throw RuntimeException("内存地址${index}的值未设置")
        return data.clone()
    }

    operator fun get(x: Int, y: Int) : Data {
        val index = getIndex(x, y)
        return this[index]
    }

    operator fun get(name: String) : Data {
        val index = getIndex(name)
        return this[index]
    }

    operator fun set(index: Int, data: Data) {
        if (index < 0 || index >= size) {
            throw RuntimeException("无效的下标：$index")
        }
        datas[index] = data.clone()
    }

    operator fun set(x: Int, y: Int, data: Data) {
        val index = getIndex(x, y)
        this[index] = data
    }

    operator fun set(name: String, data: Data) {
        val index = getIndex(name)
        this[index] = data
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = getIndex(x, y)
                val s = when (val data = datas[index]) {
                    is Data -> data.toString()
                    null -> "NULL"
                    else -> throw RuntimeException("无效的类型")
                }
                builder.append(s)
                if (s.length < 4) {
                    builder.append("\t")
                }
                builder.append("\t")
            }
        }
        return builder.toString()
    }
}