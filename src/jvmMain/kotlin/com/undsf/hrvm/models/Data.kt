package com.undsf.hrvm.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Data(
    type: DataType = DataType.INTEGER,
    value: Int = 0
) : Cloneable {
    val type: DataType by mutableStateOf(type)
    var value: Int by mutableStateOf(value)

    constructor(intValue: Int) : this(DataType.INTEGER, intValue)
    constructor(charValue: Char) : this(DataType.CHARACTER, charValue.code)

    operator fun inc() : Data {
        if (type == DataType.CHARACTER) {
            throw RuntimeException("字符类型无法进行自增运算")
        }
        value++
        return this
    }

    operator fun dec() : Data {
        if (type == DataType.CHARACTER) {
            throw RuntimeException("字符类型无法进行自减运算")
        }
        value--
        return this
    }

    operator fun plus(other: Data) : Data {
        if (type == DataType.CHARACTER || other.type == DataType.CHARACTER) {
            throw RuntimeException("字符类型无法进行加法运算")
        }
        return Data(type, value + other.value)
    }

    operator fun plus(other: Int) : Data {
        if (type == DataType.CHARACTER) {
            throw RuntimeException("字符类型无法进行加法运算")
        }
        return Data(type, value + other)
    }

    operator fun minus(other: Data) : Data {
        if (type != other.type) {
            throw RuntimeException("被减数与减数类型不同，无法进行减法运算")
        }
        // 减法运算的结果只会是数值
        return Data(DataType.INTEGER, value - other.value)
    }

    operator fun minus(other: Int) : Data {
        if (type != DataType.INTEGER) {
            throw RuntimeException("被减数与减数类型不同，无法进行减法运算")
        }
        // 减法运算的结果只会是数值
        return Data(DataType.INTEGER, value - other)
    }

    public override fun clone(): Data {
        return Data(type, value)
    }

    override fun toString(): String {
        return when (type) {
            DataType.INTEGER -> "$value";
            DataType.CHARACTER -> "'${value.toChar()}'"
        }
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Int -> {
                type == DataType.INTEGER && value == other
            }
            is Char -> {
                type == DataType.CHARACTER && value == other.code
            }
            is Data -> {
                type == other.type && value == other.value
            }
            null -> false
            else -> false
        }
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}