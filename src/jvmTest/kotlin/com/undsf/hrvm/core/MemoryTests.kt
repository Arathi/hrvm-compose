package com.undsf.hrvm.core

import kotlin.test.assertEquals

class MemoryTests {
    fun test() {
        val m1 = Memory()
        assertEquals(1, m1.size)

        val m4_4 = Memory(4, 4)
        assertEquals(16, m4_4.size)

        val mInit = Memory(
            3, 3,
            Data(), '1', 2,
            3, null, '5',
            Data(6), Data('7'), Data(8)
        )
        assertEquals(9, mInit.size)

    }
}