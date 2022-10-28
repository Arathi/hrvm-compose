package com.undsf.hrvm.models

class Problem(
    var title: String = "",
    var descriptions: String = "",
    var input: MutableList<Any> = mutableListOf(),
    var output: MutableList<Any> = mutableListOf(),
    var memoryWidth: Int = 1,
    var memoryHeight: Int = 1
) {
    companion object {
        val Problems = listOf<Problem>(
            // 0
            Problem(
                "测试问题",
                "测试问题描述",
                mutableListOf(1, 2, 'A', 'B', 'F', 'C', 4, 3),
                mutableListOf(2, 'B', 'F', 4),
                4,
                4
            ),

            // 1
            Problem(
                "收发室",
                "",
                mutableListOf(8, 8, 9),
                mutableListOf(8, 8, 9)
            ),

            // 2
            Problem(
                "繁忙的收发室",
                "",
                mutableListOf('B', 'O', 'O', 'T', 'S', 'E', 'Q', 'U', 'E', 'N', 'C', 'E'),
                mutableListOf('B', 'O', 'O', 'T', 'S', 'E', 'Q', 'U', 'E', 'N', 'C', 'E')
            )
        )

        val default = Problems[0]
    }
}