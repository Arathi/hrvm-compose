package com.undsf.hrvm.exceptions

import kotlin.RuntimeException

class AssemblyException(message: String) : RuntimeException(message) {
    constructor(lineNo: Int, source: String, message: String) : this("${message}: 第${lineNo}行：${source}")
}