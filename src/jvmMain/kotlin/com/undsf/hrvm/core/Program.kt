package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

class Program(
    val variables: MutableMap<String, Int> = mutableMapOf(),
    val labels: MutableMap<String, Int> = mutableMapOf(),
    val instructs: MutableList<Instruct> = mutableListOf()
) {
    val size get() = instructs.size

    operator fun get(label: String) : Instruct {
        if (labels.containsKey(label)) {
            val index = labels[label]
            if (index != null) {
                return this[index]
            }
            throw RuntimeException("标签${label}定义出错")
        }
        throw RuntimeException("标签${label}不存在")
    }

    operator fun get(index: Int): Instruct {
        if (index < 0 || index >= instructs.size) {
            throw RuntimeException("指令${index}不存在")
        }
        return instructs[index]
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (entry in variables.entries) {
            val name = entry.key
            val value = entry.value
            builder.appendLine(".set $name $value")
        }
        for (inst in instructs) {
            builder.appendLine(inst)
        }
        return builder.toString()
    }
}