package com.undsf.hrvm.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import mu.KotlinLogging
import com.undsf.hrvm.exceptions.RuntimeException

private val logger = KotlinLogging.logger {}

class Processor(val machine: Machine) {
    var acc: Data? by mutableStateOf(null)
    var pc: Int by mutableStateOf(0)
    var counter: Int by mutableStateOf(0)
    var status: ProcessorStatus by mutableStateOf(ProcessorStatus.Pending)

    fun reset() {
        acc = null
        pc = 0
        counter = 0
        status = ProcessorStatus.Pending
    }

    fun step() {
        status = ProcessorStatus.Running
        val inst = machine.getInstruction(pc++) ?: throw RuntimeException("指令获取失败")
        execute(inst)
        if (status != ProcessorStatus.Terminated) {
            status = ProcessorStatus.Paused
        }
    }

    fun run() {
        status = ProcessorStatus.Running
        do {
            val inst = machine.getInstruction(pc++) ?: throw RuntimeException("指令获取失败")
            execute(inst)
        }
        while (status != ProcessorStatus.Terminated && status != ProcessorStatus.Paused)
        logger.info { "指令全部运行完成" }
    }

    fun execute(inst: Instruction) {
        try {
            when (inst.oper) {
                "PLA" -> pla()
                "PHA" -> pha()
                "LDA" -> lda(inst.data!!, inst.indirect)
                "STA" -> sta(inst.data!!, inst.indirect)
                "ADD" -> add(inst.data!!, inst.indirect)
                "SUB" -> sub(inst.data!!, inst.indirect)
                "INC" -> inc(inst.data!!, inst.indirect)
                "DEC" -> dec(inst.data!!, inst.indirect)
                "JMP" -> jmp(inst.data!!)
                "BEQ" -> beq(inst.data!!)
                "BMI" -> bmi(inst.data!!)
            }
        }
        catch (ex: Exception) {
            status = ProcessorStatus.Terminated
            logger.error { ex.message }
            machine.errorMessage = ex.message
        }
    }

    fun pla() {
        pc++
        counter++
        acc = machine.popFromInbox()
        logger.info { "acc = inbox.pop() // $acc" }

        if (acc == null) {
            logger.info { "inbox取出空值，运行结束" }
            status = ProcessorStatus.Terminated
        }
    }

    fun pha() {
        if (acc != null) {
            pc++
            counter++
            machine.pushToOutbox(acc!!)
            logger.info { "outbox.push(acc) // acc=$acc" }

            acc = null
            logger.info { "acc = null" }
        }
        else {
            throw RuntimeException("累加器中没有值")
        }
    }

    fun indirectAddressing(addr: Int): Int {
        val realAddrData = machine.readFromMemory(addr) ?:
            throw RuntimeException("${addr}中的没有值，无法间接寻址")
        if (realAddrData.type != DataType.INTEGER) {
            throw RuntimeException("${addr}中的值不是整型，无法间接寻址")
        }
        return realAddrData.value
    }

    fun lda(addr: Int, indirect: Boolean = false) {
        val offset = if (indirect) indirectAddressing(addr) else addr
        pc++
        counter++
        acc = machine.readFromMemory(offset)

        if (indirect) {
            logger.info { "acc = memory[memory[$addr]] // memory[$offset]=$acc" }
        }
        else {
            logger.info { "acc = memory[$offset] // $acc" }
        }
    }

    fun sta(addr: Int, indirect: Boolean = false) {
        if (acc != null) {
            pc++
            counter++
            val offset = if (indirect) indirectAddressing(addr) else addr
            machine.writeToMemory(offset, acc)

            if (indirect) {
                logger.info { "memory[memory[$addr]] = acc // memory[$offset]=$acc" }
            }
        }
        else {
            throw RuntimeException("累加器中没有值")
        }
    }

    fun add(addr: Int, indirect: Boolean = false) {
        if (acc != null) {
            pc++
            counter++
            val offset = if (indirect) indirectAddressing(addr) else addr
            val data = machine.readFromMemory(offset) ?: throw RuntimeException("内存${offset}中没有值")
            acc = acc!! + data
        }
        else {
            throw RuntimeException("累加器中没有值")
        }
    }

    fun sub(addr: Int, indirect: Boolean = false) {
        if (acc != null) {
            pc++
            counter++
            val offset = if (indirect) indirectAddressing(addr) else addr
            val data = machine.readFromMemory(offset) ?: throw RuntimeException("内存${offset}中没有值")
            acc = acc!! - data
        }
        else {
            throw RuntimeException("累加器中没有值")
        }
    }

    fun inc(addr: Int, indirect: Boolean = false) {
        val offset = if (indirect) indirectAddressing(addr) else addr
        acc = machine.readFromMemory(offset)
        if (acc != null) {
            pc++
            counter++
            acc = acc!! + 1
            machine.writeToMemory(offset, acc)
        }
        else {
            throw RuntimeException("地址${offset}中的值无效")
        }
    }

    fun dec(addr: Int, indirect: Boolean = false) {
        val offset = if (indirect) indirectAddressing(addr) else addr
        acc = machine.readFromMemory(offset)
        if (acc != null) {
            pc++
            counter++
            acc = acc!! - 1
            machine.writeToMemory(offset, acc)
        }
        else {
            throw RuntimeException("地址${offset}中的值无效")
        }
    }

    fun jmp(addr: Int) {
        pc = addr
        counter++
        logger.info { "goto $pc" }
    }

    fun beq(addr: Int) {
        if (acc != null) {
            logger.info { "if (acc == 0) goto line_$addr" }
            counter++
            if (acc!!.type == DataType.INTEGER) {
                if (acc!!.value == 0) {
                    pc = addr
                }
            }
            else {
                throw RuntimeException("累加器中的值的类型不为整数")
            }
        }
        else {
            throw RuntimeException("累加器中没有值")
        }
    }

    fun bmi(addr: Int) {
        if (acc != null) {
            logger.info { "if (acc < 0) goto line_$addr" }
            counter++
            if (acc!!.type == DataType.INTEGER) {
                if (acc!!.value < 0) {
                    pc = addr
                }
            }
            else {
                throw RuntimeException("累加器中的值的类型不为整数")
            }
        }
        else {
            throw RuntimeException("累加器中没有值")
        }
    }
}