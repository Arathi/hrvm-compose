package com.undsf.hrvm.core

enum class Addressing {
    ACCUMULATOR, // 累加器寻址 PLA、PHA
    ABSOLUTE,    // 绝对寻址 JMP、BEQ、BMI
    INDIRECT,    // 间接寻址
}