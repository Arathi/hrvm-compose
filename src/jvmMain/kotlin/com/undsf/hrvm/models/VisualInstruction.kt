package com.undsf.hrvm.models

import androidx.compose.ui.graphics.Color
import com.undsf.hrvm.views.InstructionBlockColorCompute
import com.undsf.hrvm.views.InstructionBlockColorJump
import com.undsf.hrvm.views.InstructionBlockColorMemory
import com.undsf.hrvm.views.InstructionBlockColorQueue

class VisualInstruction(
    oper: String = OPER_NAME_NOP,
    data: Int? = null,
    indirect: Boolean = false,
    label: String? = null,
    param: String? = null
) : IntermediateInstruction(
    oper,
    data,
    indirect,
    label,
    param
) {
    val operName: String get() {
        return when (oper) {
            "PLA" -> "inbox->"
            "PHA" -> "->outbox"
            "LDA" -> "copyfrom"
            "STA" -> "copyto"
            "ADD" -> "add"
            "SUB" -> "sub"
            "INC" -> "bump+"
            "DEC" -> "bump-"
            "JMP" -> "jump"
            "BEQ" -> "jump if zero"
            "BMI" -> "jump if neg"
            else -> "无效的指令！"
        }
    }

    val operData: Any? get() {
        return when {
            param != null -> param
            data != null -> data
            else -> null
        }
    }

    val color: Color get() {
        return when (oper) {
            "PLA", "PHA" -> InstructionBlockColorQueue
            "LDA", "STA" -> InstructionBlockColorMemory
            "ADD", "SUB", "INC", "DEC" -> InstructionBlockColorCompute
            "JMP", "BEQ", "BMI" -> InstructionBlockColorJump
            else -> Color.White
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        if (isLabel) {
            builder.append(label)
            builder.append(":")
        }
        else {
            builder.append("\t")
            builder.append(operName)
            if (operData != null) {
                builder.append(" ")
                builder.append(operData)
            }
        }
        return builder.toString()
    }
}
