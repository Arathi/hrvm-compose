package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.AssemblyException
import java.time.Instant

class Instruct(
    var opCode : OpCode = OpCode.NOP,
    var value : Int? = null,
    var valueStr: String? = null,
    var addressing: Addressing = Addressing.ABSOLUTE) {

    var label : String? = null
    var lineNo : Int? = null
    var source: String? = null

    override fun toString(): String {
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

        val labelStr = if (label != null) "$label: \n" else ""
        return "$labelStr\t$opCodeStr $valueStrBuilder"
    }

    companion object {
        val PLA = Instruct(OpCode.PLA)

        val PHA = Instruct(OpCode.PHA)

        fun LDA(address: String, indirect: Boolean = false) : Instruct {
            return Instruct(OpCode.LDA)
        }

        fun JMP(address: Int) : Instruct {
            return Instruct(OpCode.LDA, address)
        }
    }
}