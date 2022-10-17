package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.RuntimeException

class Outbox : DataQueue() {
    override fun pop(): Data? {
        throw RuntimeException("无法从输出队列获取数据")
    }
}