package com.undsf.hrvm.models

import kotlin.test.Test
import kotlin.test.assertNotNull

class AssemblerTests {
    @Test
    fun testAssemble() {
        val sources = """
.set first 0
.set second 1

start:
    PLA
    STA first
    PLA
    STA second

compare:
    SUB first
    BMI loadSecond

loadFirst:
    LDA first
    JMP output

loadSecond:
    LDA second

output:
    PHA
    JMP start
""".trimIndent()
        val hrasm = Assembler()
        val program = hrasm.assemble(sources, true)
        assertNotNull(program)
    }
}