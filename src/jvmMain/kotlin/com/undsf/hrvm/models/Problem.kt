package com.undsf.hrvm.models

class Problem(
    var descriptions: String = "",
    var input: MutableList<Any> = mutableListOf(),
    var output: MutableList<Any> = mutableListOf(),
    var memoryWidth: Int = 1,
    var memoryHeight: Int = 1
) {
    companion object {
        val Problems = listOf<Problem>(
            // 0
            Problem(),

            // 1
            Problem(
                "",
                mutableListOf(8, 8, 9),
                mutableListOf(8, 8, 9)
            ),

            // 2
            Problem(

            )
        )
    }
}