package com.undsf.hrvm.models

class ProcessorTests {
    fun testRun() {
        val machine = Machine(
            listOf(),
            listOf(),
            4,
            1
        )

        val processor = machine.processor

        processor.run {

        }
    }
}