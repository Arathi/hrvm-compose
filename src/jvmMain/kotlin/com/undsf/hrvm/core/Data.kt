package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

data class Data(
    var type: DataType = DataType.INTEGER,
    var value: Int = 0
) : Cloneable {
    constructor(intValue: Int) : this(DataType.INTEGER, intValue)
    constructor(charValue: Char) : this(DataType.CHARACTER, charValue.code)

    operator fun inc() : Data {
        value++
        return this.clone()
    }

    operator fun dec() : Data {
        value--
        return this.clone()
    }

    operator fun plus(other: Data) : Data {
        val result = Data(type, value + other.value)
        if (other.type == DataType.CHARACTER) {
            result.type = other.type
        }
        return result
    }

    operator fun minus(other: Data) : Data {
        if (type == DataType.INTEGER && other.type == DataType.CHARACTER) {
            throw RuntimeException("被减数类型为int时，减数类型不能为char")
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
}