package com.undsf.hrvm.models

open class Instruction(
    val oper: String = OPER_NAME_NOP,
    open val data: Int? = null,
    val indirect: Boolean = false
) {
    val executable: Boolean get() {
        return when (oper) {
            "PLA", "PHA",
            "LDA", "STA",
            "ADD", "SUB",
            "INC", "DEC",
            "JMP", "BEQ", "BMI" -> true
            else -> false
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        if (oper != OPER_NAME_NOP) {
            builder.append(oper)
            builder.append(" ")
            if (data != null) {
                if (indirect) builder.append("(")
                builder.append(data)
                if (indirect) builder.append(")")
            }
        }
        return builder.toString()
    }

    companion object {
        const val OPER_NAME_NOP = "NOP"

        val NOP = Instruction()
        val PLA = Instruction("PLA")
        val PHA = Instruction("PHA")
        fun LDA(addr: Int, indirect: Boolean = false) = Instruction("LDA", addr, indirect)
        fun STA(addr: Int, indirect: Boolean = false) = Instruction("STA", addr, indirect)
        fun ADD(addr: Int, indirect: Boolean = false) = Instruction("ADD", addr, indirect)
        fun SUB(addr: Int, indirect: Boolean = false) = Instruction("SUB", addr, indirect)
        fun INC(addr: Int, indirect: Boolean = false) = Instruction("INC", addr, indirect)
        fun DEC(addr: Int, indirect: Boolean = false) = Instruction("DEC", addr, indirect)
        fun JMP(addr: Int) = Instruction("JMP", addr)
        fun BEQ(addr: Int) = Instruction("BEQ", addr)
        fun BMI(addr: Int) = Instruction("BMI", addr)
    }
}