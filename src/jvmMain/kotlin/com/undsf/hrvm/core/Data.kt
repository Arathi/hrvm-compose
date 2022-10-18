package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

data class Data(
    var type: DataType = DataType.INTEGER,
    var value: Int = 0
) : Cloneable {
    constructor(intValue: Int) : this(DataType.INTEGER, intValue)
    constructor(charValue: Char) : this(DataType.CHARACTER, charValue.code)

    operator fun inc() : Data {
        if (type == DataType.CHARACTER) {
            throw RuntimeException("字符类型无法进行自增运算")
        }
        value++
        return this.clone()
    }

    operator fun dec() : Data {
        if (type == DataType.CHARACTER) {
            throw RuntimeException("字符类型无法进行自减运算")
        }
        value--
        return this.clone()
    }

    operator fun plus(other: Data) : Data {
        if (type == DataType.CHARACTER || other.type == DataType.CHARACTER) {
            throw RuntimeException("字符类型无法进行加法运算")
        }
        return Data(type, value + other.value)
    }

    operator fun minus(other: Data) : Data {
        if (type != other.type) {
            throw RuntimeException("被减数与减数类型不同，无法进行减法运算")
        }
        return Data(type, value - other.value)
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
}