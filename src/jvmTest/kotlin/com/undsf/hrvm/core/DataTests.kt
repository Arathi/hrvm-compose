package com.undsf.hrvm.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataTests {
    @Test
    fun testConstructors() {
        val d512 = Data(512)
        assertTrue { d512.equals(512) }

        val dm999 = Data(-999)
        assertTrue { dm999.equals(-999) }

        val dB = Data('B')
        assertTrue { dB.equals('B') }
    }

    @Test
    fun testIntegerCompute() {
        var d512 = Data(512)
        assertEquals(DataType.INTEGER, d512.type)
        assertEquals(512, d512.value)
        val d512a = d512.clone()

        val d513 = d512++
        assertEquals(513, d513.value)

        val d514 = ++d512
        assertEquals(514, d514.value)

        val d515 = d512a + Data(3)
        assertEquals(515, d515.value)

        var d500 = d515 - Data(15)
        assertEquals(500, d500.value)

        val d499 = d500--
        assertEquals(499, d499.value)
        --d500
        assertEquals(498, d500.value)
    }
}