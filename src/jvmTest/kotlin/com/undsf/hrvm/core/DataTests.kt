package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DataTests {
    @Test
    fun testConstructors() {
        val d512 = Data(512)
        assertEquals("512", d512.toString())

        val dm999 = Data(-999)
        assertEquals("-999", dm999.toString())

        val dB = Data('B')
        assertEquals("'B'", dB.toString())
    }

    @Test
    fun testCompute() {
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

        var dA = Data('A')
        assertEquals(DataType.CHARACTER, dA.type)
        assertEquals('A', dA.value.toChar())

        var dB = dA++
        assertEquals('B', dA.value.toChar())
        assertEquals('B', dB.value.toChar())

        val dAa = dB--
        assertEquals('A', dAa.value.toChar())

        val dD = dAa + Data(3)
        assertEquals('D', dD.value.toChar())

        val dC = dD - Data(1)
        assertEquals('C', dC.value.toChar())

        val dF = Data(2) + dD
        assertEquals(DataType.CHARACTER, dF.type)
        assertEquals('F', dF.value.toChar())

        var ex: RuntimeException? = null
        try {
            val dxx = Data(1024) - Data('A')
        }
        catch (e: RuntimeException) {
            ex = e
        }
        assertNotNull(ex)
    }
}