package com.undsf.hrvm.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.undsf.hrvm.exceptions.RuntimeException
import mu.KotlinLogging

private var logger = KotlinLogging.logger {}

class Machine(
    inboxSamples: List<Any>,
    outboxSamples: List<Any>,
    memoryWidth: Int = 1,
    memoryHeight: Int = 1
) {
    val processor by mutableStateOf(Processor(this))
    val inbox by mutableStateOf(Queue(inboxSamples, QueueType.Inbox))
    val outbox by mutableStateOf(Queue(outboxSamples, QueueType.Outbox))
    val memory by mutableStateOf(Memory(memoryWidth, memoryHeight))
    var program: Program? by mutableStateOf(null)
    var errorMessage: String? by mutableStateOf(null)

    val status: ProcessorStatus get() = processor.status

    fun reset() {
        inbox.reset()
        outbox.reset()
        memory.reset()
        errorMessage = null
        processor.reset()
    }

    fun readFromMemory(index: Int): Data? {
        return memory.read(index)
    }

    fun writeToMemory(index: Int, data: Data?) {
        memory.write(index, data)
    }

    fun popFromInbox() : Data? {
        val data = inbox.pop()
        return data
    }

    fun pushToOutbox(data: Data) {
        outbox.push(data)
        // TODO 检查是否与参考输出一致
    }

    fun loadProgram(program: Program) {
        this.program = program
        logger.info { "程序已加载" }
    }

    fun getInstruction(pc: Int) : Instruction? {
        if (program == null) {
            throw RuntimeException("程序未编译完成，无法取指令")
        }
        return program!!.getInstruction(pc)
    }
}