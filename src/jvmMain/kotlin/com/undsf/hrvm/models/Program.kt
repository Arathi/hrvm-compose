package com.undsf.hrvm.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

class Program {
    val variableNameToAddress = mutableStateMapOf<String, Int>()
    val variableAddressToName = mutableStateMapOf<Int, String>()

    val labelNameToAddress = mutableStateMapOf<String, Int>()
    val labelAddressToName = mutableStateMapOf<Int, String>()

    val instructions = mutableStateListOf<Instruction>()

    val length: Int get() {
        var counter = 0
        for (inst in instructions) {
            if (inst.executable) counter++
        }
        return counter
    }

    fun addVariable(variableName: String, address: Int) {
        variableNameToAddress[variableName] = address
        variableAddressToName[address] = variableName
    }

    fun addLabel(label: String) {
        val addr = instructions.size
        labelNameToAddress[label] = addr
        labelAddressToName[addr] = label
        instructions.add(Instruction.NOP)
    }

    fun addInstruction(inst: Instruction) {
        instructions.add(inst)
    }

    fun getInstruction(addr: Int) : Instruction? {
        if (addr in instructions.indices) {
            return instructions[addr]
        }
        return null
    }

    fun deassemble() : String {
        return ""
    }

    override fun toString(): String {
        return deassemble()
    }
}

