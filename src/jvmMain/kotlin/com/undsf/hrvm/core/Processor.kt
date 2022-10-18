package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException
import mu.KotlinLogging
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger {}

class Processor(
    val inbox: Inbox = Inbox(),
    val memory: Memory = Memory(),
    var program: Program = Program(),
    val outbox: Outbox = Outbox()
) {
    var acc: Data? = null
    var pc: Int = 0
    var status: ProcessorStatus = ProcessorStatus.Pending
    val stepCounter: AtomicInteger = AtomicInteger(0)

    fun reset() {
        inbox.reset()
        memory.reset()
        acc = null
        pc = 0
        stepCounter.set(0)
    }

    open fun read(index: Int) : Data {
        return memory[index]
    }

    open fun write(index: Int, data: Data) {
        memory[index] = data
    }

    fun load(program: Program) {
        this.program = program
    }

    fun run(interval: Long = 100) {
        status = ProcessorStatus.Running
        do {
            step()
            TimeUnit.MILLISECONDS.sleep(interval)
        }
        while (status == ProcessorStatus.Running)
    }

    fun step() {
        val inst = program[pc++]
        exec(inst)
        if (status == ProcessorStatus.Running) {
            stepCounter.incrementAndGet()
        }
    }

    fun exec(inst: Instruct) {
        when (inst.opCode) {
            OpCode.PLA -> pla()
            OpCode.PHA -> pha()
            OpCode.LDA -> lda(inst.value!!, inst.addressing == Addressing.INDIRECT)
            OpCode.STA -> sta(inst.value!!, inst.addressing == Addressing.INDIRECT)
            OpCode.ADD -> add(inst.value!!, inst.addressing == Addressing.INDIRECT)
            OpCode.SUB -> sub(inst.value!!, inst.addressing == Addressing.INDIRECT)
            OpCode.INC -> inc(inst.value!!, inst.addressing == Addressing.INDIRECT)
            OpCode.DEC -> dec(inst.value!!, inst.addressing == Addressing.INDIRECT)
            OpCode.JMP -> jmp(inst.value!!)
            OpCode.BEQ -> beq(inst.value!!)
            OpCode.BMI -> bmi(inst.value!!)
            else -> throw RuntimeException("执行了无效的指令：$inst")
        }
    }

    fun indirectAddressing(index: Int) : Int {
        try {
            val data = memory[index]
            if (data.type == DataType.CHARACTER) {
                throw RuntimeException("内存地址${index}中的值为字符，无法进行间接寻址")
            }
            return data.value
        }
        catch (ex: RuntimeException) {
            throw RuntimeException("内存地址${index}取值失败，无法进行间接寻址")
        }
    }

    fun pla() {
        val data = inbox.pop()
        if (data == null) {
            logger.info { "输入队列的数据已经全部取出" }
            status = ProcessorStatus.Stopped
        }
        acc = data
        logger.info { "acc = inbox() = $acc" }
    }

    fun pha() {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法输出")
        }
        outbox.push(acc!!)
        acc = null
        logger.info { "outbox($acc)" }
    }

    fun lda(index: Int, indirect: Boolean = false) {
        var offset = index
        if (indirect) {
            offset = indirectAddressing(index)
        }
        acc = read(offset)
        if (indirect) {
            logger.info { "acc = memory[memory[${index}]]) = $acc" }
        }
        else {
            logger.info { "acc = memory(${offset}) = $acc" }
        }
    }

    fun sta(index: Int, indirect: Boolean = false) {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法写入内存")
        }

        var offset = index
        if (indirect) {
            offset = indirectAddressing(index)
        }
        write(offset, acc!!)
        if (indirect) {
            logger.info { "memory[memory[$index] = acc = $acc" }
        }
        else {
            logger.info { "memory[${offset}] = acc = $acc" }
        }
    }

    fun add(index: Int, indirect: Boolean = false) {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法执行加法运算")
        }

        var offset = index
        if (indirect) {
            offset = indirectAddressing(index)
        }
        val data = read(offset)

        acc = acc!! + data
        if (indirect) {
            logger.info { "acc += memory[memory[$index]] // $acc" }
        }
        else {
            logger.info { "acc += memory[$offset] // $acc" }
        }
    }

    fun sub(index: Int, indirect: Boolean = false) {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法执行减法运算")
        }

        var offset = index
        if (indirect) {
            offset = indirectAddressing(index)
        }
        val data = read(offset)

        acc = acc!! - data
        if (indirect) {
            logger.info { "acc -= memory[memory[$index]] // $acc" }
        }
        else {
            logger.info { "acc -= memory[$offset] // $acc" }
        }
    }

    fun inc(index: Int, indirect: Boolean = false) {
        var offset = index
        if (indirect) {
            offset = indirectAddressing(index)
        }
        var data = read(offset)
        data++
        write(offset, data)
        acc = data

        if (indirect) {
            logger.info { "acc = ++memory[memory[$index]] // $acc" }
        }
        else {
            logger.info { "acc = ++memory[$offset] // $acc" }
        }
    }

    fun dec(index: Int, indirect: Boolean = false) {
        var offset = index
        if (indirect) {
            offset = indirectAddressing(index)
        }
        var data = read(offset)
        data--
        write(offset, data)
        acc = data

        if (indirect) {
            logger.info { "acc = --memory[memory[$index]] // $acc" }
        }
        else {
            logger.info { "acc = --memory[$offset] // $acc" }
        }
    }

    fun jmp(address: Int) {
        pc = address
        logger.info { "pc = $address" }
    }

    fun beq(address: Int) {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法执行条件跳转")
        }
        if (acc?.value == 0) {
            pc = address
        }
        logger.info { "if (acc == 0) pc = $address" }
    }

    fun bmi(address: Int) {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法执行条件跳转")
        }
        if (acc?.value!! < 0) {
            pc = address
        }
        logger.info { "if (acc < 0) pc = $address" }
    }
}