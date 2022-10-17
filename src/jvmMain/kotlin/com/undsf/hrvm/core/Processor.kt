package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Processor(
    val inbox: Inbox = Inbox(),
    val memory: Memory = Memory(),
    val program: Program = Program(),
    val outbox: Outbox = Outbox()
) {
    var acc: Data? = null
    var pc: Int = 0
    var status: ProcessorStatus = ProcessorStatus.Pending

    fun reset() {
        inbox.reset()
        memory.reset()
    }

    open fun read(index: Int) : Data {
        return memory[index]
    }

    open fun write(index: Int, data: Data) {
        memory[index] = data
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
    }

    fun pha() {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法输出")
        }
        outbox.push(acc!!)
        acc = null
    }

    fun lda(index: Int, indirect: Boolean = false) {
        var offset = index
        if (indirect) {
            offset = indirectAddressing(index)
        }
        acc = read(offset)
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
    }

    fun jmp(address: Int) {
        pc = address
    }

    fun beq(address: Int) {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法执行条件跳转")
        }
        if (acc?.value == 0) {
            jmp(address)
        }
    }

    fun bmi(address: Int) {
        if (acc == null) {
            throw RuntimeException("累加器中没有值，无法执行条件跳转")
        }
        if (acc?.value!! < 0) {
            jmp(address)
        }
    }
}