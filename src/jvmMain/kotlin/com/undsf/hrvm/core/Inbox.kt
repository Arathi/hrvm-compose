package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

class Inbox(vararg data: Any?) : DataQueue(*data) {
    override fun push(data: Data) {
        throw RuntimeException("无法向输入队列推送数据")
    }
}