package com.undsf.hrvm.models

import kotlin.test.Test
import kotlin.test.assertEquals

class DataTests {
    @Test
    fun test() {
        var data0 = Data(999)
        val data1 = Data(1000)
        val data2 = data0++
        assertEquals(data0, data1)
        assertEquals(data1, data2)
        assertEquals(data0, data2)

        val dataA = Data('A')
        val dataC = Data('C')
        val dataDeltaExpected = Data(2)
        val dataDelta = dataC - dataA
        assertEquals(dataDeltaExpected, dataDelta)
    }
}