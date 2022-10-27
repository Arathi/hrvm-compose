package com.undsf.hrvm.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

class Memory(
    val width: Int = 1,
    val height: Int = 1,
    val initDatas: Map<Int, Any> = mapOf()
) {
    val size: Int get() { return width * height }
    val datas: MutableList<Data?> = mutableStateListOf()
    val variables: MutableMap<String, Int> = mutableStateMapOf()

    val variableNames: MutableMap<Int, String> get() {
        val vars: MutableMap<Int, String> = mutableMapOf()
        for (entry in variables.entries) {
            vars[entry.value] = entry.key
        }
        return vars
    }

    init {
        reset()
    }

    fun reset() {
        datas.clear()

        for (i in 0 until size) {
            datas.add(null)
        }

        for (entry in initDatas) {
            val index = entry.key
            val value = entry.value
            val data: Data? = when (value) {
                is Data -> value.clone()
                is Int -> Data(value)
                is Char -> Data(value)
                else -> null
            }
            if (data != null) {
                datas[index] = data
            }
        }
    }

    fun getIndexByXY(x: Int, y: Int): Int {
        return x + y * width
    }

    fun registerVariable(variableName: String, index: Int) {
        variables[variableName] = index
    }

    fun unregisterVariable(variableName: String) {
        variables.remove(variableName)
    }

    fun unregisterVariable(index: Int) {
        val keys = mutableSetOf<String>()
        for (entry in variables.entries) {
            if (entry.value == index) {
                keys.add(entry.key)
            }
        }
        for (key in keys) {
            variables.remove(key)
        }
    }

    fun unregisterAllVariables() {
        variables.clear()
    }

    fun write(index: Int, v: Int) {
        write(index, Data(v))
    }

    fun write(index: Int, ch: Char) {
        write(index, Data(ch))
    }

    fun write(index: Int, data: Data?) {
        datas[index] = data?.clone()
    }

    fun read(index: Int) : Data? {
        return datas[index]
    }

    operator fun get(index: Int) : Data? = read(index)
    operator fun set(index: Int, data: Data?) = write(index, data)
    operator fun set(index: Int, v: Int) = write(index, v)
    operator fun set(index: Int, ch: Char) = write(index, ch)
}


