package com.undsf.hrvm.models

/**
 * 中间指令
 */
class IntermediateInstruction(
    oper: String = OPER_NAME_NOP,
    override var data: Int? = null,
    indirect: Boolean = false,
    var label: String? = null,
    var param: String? = null
) : Instruction(
    oper,
    data,
    indirect
) {
    var lineNo: Int? = null
    var source: String? = null

    fun toInstruction() : Instruction {
        return Instruction(oper, data, indirect)
    }

    companion object {
        fun Label(label: String) = IntermediateInstruction(
            oper = OPER_NAME_NOP,
            label = label
        )
    }
}