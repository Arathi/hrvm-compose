package com.undsf.hrvm.core.exceptions

import com.undsf.hrvm.core.Data
import kotlin.RuntimeException

class WrongAnswerException(expected: Data, actual: Data, message: String) : RuntimeException("${message}，预期：${expected}，实际：${actual}")