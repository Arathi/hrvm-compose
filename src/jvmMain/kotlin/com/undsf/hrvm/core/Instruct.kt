package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.AssemblyException
import java.time.Instant

class Instruct(
    var opCode : OpCode = OpCode.NOP,
    var value : Int? = null,
    var addressing: Addressing = Addressing.ABSOLUTE,
    var labels : MutableSet<String>? = null,
    var valueStr: String? = null,
    var lineNo : Int? = null,
    var source: String? = null) {

    override fun toString(): String {
        return toString(true, "    ")
    }

    fun toString(displayLabel: Boolean, tab: String = "\t") : String {
        val labelStrBuilder = StringBuilder()
        if (displayLabel && labels != null) {
            for (label in labels!!) {
                labelStrBuilder.appendLine("${label}:")
            }
        }

        val opCodeStr = when (opCode) {
            OpCode.PLA -> "PLA"
            OpCode.PHA -> "PHA"
            OpCode.LDA -> "LDA"
            OpCode.STA -> "STA"
            OpCode.ADD -> "ADD"
            OpCode.SUB -> "SUB"
            OpCode.INC -> "INC"
            OpCode.DEC -> "DEC"
            OpCode.JMP -> "JMP"
            OpCode.BEQ -> "BEQ"
            OpCode.BMI -> "BMI"
            else -> throw AssemblyException("无效的指令")
        }

        val valueStrBuilder = StringBuilder()
        if (addressing == Addressing.INDIRECT) valueStrBuilder.append("[")
        if (valueStr != null) {
            valueStrBuilder.append(valueStr)
        }
        else if (value != null) {
            valueStrBuilder.append(value)
        }
        if (addressing == Addressing.INDIRECT) valueStrBuilder.append("]")

        return "$labelStrBuilder$tab$opCodeStr $valueStrBuilder"
    }

    companion object {
        val PLA = Instruct(OpCode.PLA)

        val PHA = Instruct(OpCode.PHA)

        fun LDA(address: String, indirect: Boolean = false) : Instruct {
            return Instruct(OpCode.LDA)
        }

        fun JMP(address: Int) : Instruct = Instruct(OpCode.JMP, address)
        fun BEQ(address: Int) : Instruct = Instruct(OpCode.BEQ, address)
        fun BMI(address: Int) : Instruct = Instruct(OpCode.BMI, address)
    }
}