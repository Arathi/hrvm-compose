package com.undsf.hrvm.core

import kotlin.test.*

class ProcessorTests {
    @Test
    fun testInsts() {
        val processor = Processor(
            inbox = Inbox(1, 2, 3, 4, 5, 6, 7, 8),
            memory = Memory(4, 4, 8, 'A')
        )

        processor.pla()
        // acc = 1
        assertNotNull(processor.acc)
        assertEquals(DataType.INTEGER, processor.acc!!.type)
        assertEquals(1, processor.acc!!.value)

        processor.pha()
        // acc = null
        assertNull(processor.acc)

        processor.lda(0)
        // acc = memory[0] = 8
        assertEquals(DataType.INTEGER, processor.acc!!.type)
        assertEquals(8, processor.acc!!.value)

        processor.lda(1)
        // acc = memory[1] = 'A'
        assertEquals(DataType.CHARACTER, processor.acc!!.type)
        assertEquals('A', processor.acc!!.value.toChar())

        processor.acc = Data('B')
        // acc = 'B'
        processor.sta(8)
        // memory[8] = 'B'
        var t = processor.read(8)
        assertEquals('B', t.value.toChar())

        processor.lda(0, true)
        // acc = memory[memory[0]] = memory[8] = 'B'
        assertEquals(DataType.CHARACTER, processor.acc!!.type)
        assertEquals('B', processor.acc!!.value.toChar())

        processor.acc = Data(4)
        // acc = 4
        processor.sta(4)
        // memory[4] = 4
        processor.acc = Data(5)
        // acc = 5
        processor.add(4)
        // acc = acc + memory[4] = 4 + 5 = 9
        assertEquals(DataType.INTEGER, processor.acc!!.type)
        assertEquals(9, processor.acc!!.value)

        processor.sub(0)
        // acc = acc - memory[0] = 9 - 8 = 1
        assertEquals(1, processor.acc!!.value)

        processor.add(1)
        // acc = acc + memory[1] = 1 + 'A' = 'B'
        assertEquals(DataType.CHARACTER, processor.acc!!.type)
        assertEquals('B', processor.acc!!.value.toChar())

        processor.inc(1)
        // acc = ++memory[1] = 'A' + 1 = 'B'
        assertEquals(DataType.CHARACTER, processor.acc!!.type)
        assertEquals('B', processor.acc!!.value.toChar())
        t = processor.read(1)
        assertEquals(DataType.CHARACTER, t.type)
        assertEquals('B', t.value.toChar())

        processor.dec(0)
        // acc = --memory[0] = 8 - 1 = 7
        assertEquals(DataType.INTEGER, processor.acc!!.type)
        assertEquals(7, processor.acc!!.value)
        t = processor.read(0)
        assertEquals(DataType.INTEGER, t.type)
        assertEquals(7, t.value)

        processor.jmp(16)
        // pc = 16
        assertEquals(16, processor.pc)

        processor.acc = Data(3)
        processor.beq(10)
        // acc != 0，pc不变
        assertEquals(16, processor.pc)

        processor.acc = Data(0)
        processor.beq(10)
        // acc == 0，pc = 10
        assertEquals(10, processor.pc)

        processor.bmi(24)
        // acc >= 0，pc = 10
        assertEquals(10, processor.pc)

        processor.acc = Data(-5)
        processor.bmi(24)
        // acc < 0，pc = 24
        assertEquals(24, processor.pc)
    }

    @Test
    fun testRun() {
        val hrasm = Assembler()
        val program = hrasm.assemble("""
.set first 0
.set second 1
start:
    JMP input
getFirst:
    LDA first
output:
    PHA
input:
    PLA
    STA first
    PLA
    STA second
    SUB first
    BMI getFirst
getSecond:
    LDA second
    JMP output
""".trimIndent())

        val processor = Processor(
            inbox = Inbox(1, 2, 15, 10, 'B', 'E', 6, 8),
            memory = Memory(2),
            program = program,
            outbox = Outbox(2, 15, 'E', 8)
        )

        processor.run()
        assertTrue {
            processor.outbox.size == 4
        }
    }
}