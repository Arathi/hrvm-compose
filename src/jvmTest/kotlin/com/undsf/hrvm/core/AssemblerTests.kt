package com.undsf.hrvm.core

import kotlin.test.Test
import kotlin.test.assertEquals

class AssemblerTests {
    @Test
    fun testAssemble() {
        val sources = """
.set first 0
.set second 1
start:
    JMP input
output:
    PHA
input:
    PLA
    STA first
    PLA
    STA second
    SUB first
    BMI getFirst
    LDA second
    JMP output
getFirst:
    LDA first
    JMP output
""".trimIndent()
        val hrasm = Assembler()
        val insts = hrasm.assemble(sources)
        assertEquals(12, insts.size)
    }
}