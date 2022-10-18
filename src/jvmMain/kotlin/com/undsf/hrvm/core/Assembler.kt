package com.undsf.hrvm.core

import com.undsf.hrvm.core.exceptions.AssemblyException
import java.util.regex.Matcher
import java.util.regex.Pattern

class Assembler {
    val variables = mutableMapOf<String, Int>()
    val labels = mutableMapOf<String, Int>()
    val instructs = mutableListOf<Instruct>()
    var lastLabels = mutableSetOf<String>()

    fun assemble(sources: String) : Program {
        variables.clear()
        labels.clear()
        instructs.clear()

        val lines = sources.split("\n")
        for (lineNo in 1 .. lines.size) {
            val source = lines[lineNo - 1].trim()

            // 空行
            if (source.isEmpty()) {
                continue
            }

            // 注释
            if (source.startsWith(";")) {
                continue
            }

            val inst = assembleLine(lineNo, source)
            if (inst != null) {
                instructs.add(inst)
            }
        }

        for (inst in instructs) {
            when (inst.opCode) {
                OpCode.LDA, OpCode.STA,
                OpCode.ADD, OpCode.SUB,
                OpCode.INC, OpCode.DEC -> {
                    if (inst.value == null && inst.valueStr != null) {
                        if (variables.containsKey(inst.valueStr)) {
                            inst.value = variables[inst.valueStr]
                        }
                        else {
                            throw AssemblyException(inst.lineNo!!, inst.source!!, "变量${inst.valueStr}不存在")
                        }
                    }
                }
                OpCode.JMP, OpCode.BEQ, OpCode.BMI -> {
                    if (inst.value == null && inst.valueStr != null) {
                        if (labels.containsKey(inst.valueStr)) {
                            inst.value = labels[inst.valueStr]
                        }
                        else {
                            throw AssemblyException(inst.lineNo!!, inst.source!!, "标签${inst.valueStr}不存在")
                        }
                    }
                }
                else -> {}
            }
        }

        return Program(variables, labels, instructs)
    }

    fun assembleLine(lineNo: Int, source: String) : Instruct? {
        var matcher: Matcher? = null

        // 变量
        matcher = PatternVariableDefine.matcher(source)
        if (matcher.find()) {
            val name = matcher.group(1)
            val value = matcher.group(2).toInt()
            variables[name] = value
            return null
        }

        // 标签
        matcher = PatternLabelDefine.matcher(source)
        if (matcher.find()) {
            val label = matcher.group(1)
            labels[label] = instructs.size
            lastLabels.add(label)
            return null
        }

        matcher = PatternInstruct.matcher(source)
        if (matcher.find()) {
            val opCodeStr = matcher.group(1)
            val valueStr = matcher.group(5)
            var counter = 0
            if (matcher.group(3) == "[") counter++
            if (matcher.group(7) == "]") counter++
            if (counter == 1) {
                throw AssemblyException(lineNo, source, "检测到不对称的方括号")
            }
            val indirect = counter == 2
            val inst = buildInstruct(opCodeStr, valueStr, indirect) ?: throw AssemblyException(lineNo, source, "无法识别的汇编语句")
            inst.labels = lastLabels
            inst.lineNo = lineNo
            inst.source = source
            lastLabels = mutableSetOf()
            return inst
        }

        throw AssemblyException(lineNo, source, "无法识别的汇编语句")
    }

    fun buildInstruct(opCodeStr: String, valueStr: String? = null, indirect: Boolean = false) : Instruct? {
        val inst = Instruct()
        when (opCodeStr) {
            "PLA" -> {
                inst.opCode = OpCode.PLA
                inst.addressing = Addressing.ACCUMULATOR
            }
            "PHA" -> {
                inst.opCode = OpCode.PHA
                inst.addressing = Addressing.ACCUMULATOR
            }
            "LDA" -> {
                inst.opCode = OpCode.LDA
                inst.addressing = if (indirect) Addressing.INDIRECT else Addressing.ABSOLUTE
            }
            "STA" -> {
                inst.opCode = OpCode.STA
                inst.addressing = if (indirect) Addressing.INDIRECT else Addressing.ABSOLUTE
            }
            "ADD" -> {
                inst.opCode = OpCode.ADD
                inst.addressing = if (indirect) Addressing.INDIRECT else Addressing.ABSOLUTE
            }
            "SUB" -> {
                inst.opCode = OpCode.SUB
                inst.addressing = if (indirect) Addressing.INDIRECT else Addressing.ABSOLUTE
            }
            "INC" -> {
                inst.opCode = OpCode.INC
                inst.addressing = if (indirect) Addressing.INDIRECT else Addressing.ABSOLUTE
            }
            "DEC" -> {
                inst.opCode = OpCode.DEC
                inst.addressing = if (indirect) Addressing.INDIRECT else Addressing.ABSOLUTE
            }
            "JMP" -> {
                inst.opCode = OpCode.JMP
                inst.addressing = Addressing.ABSOLUTE
            }
            "BEQ" -> {
                inst.opCode = OpCode.BEQ
                inst.addressing = Addressing.ABSOLUTE
            }
            "BMI" -> {
                inst.opCode = OpCode.BMI
                inst.addressing = Addressing.ABSOLUTE
            }
            else -> return null
        }

        if (valueStr != null) {
            try {
                inst.value = valueStr.toInt()
            }
            catch (ex: NumberFormatException) {
                inst.valueStr = valueStr
            }
        }

        return inst
    }

    companion object {
        val PatternVariableDefine = Pattern.compile("^\\.set[ \\t]+([_A-Za-z][_A-Za-z0-9]*)[ \\t]+([0-9]+)\$")
        val PatternLabelDefine = Pattern.compile("^([_A-Za-z][_A-Za-z0-9]*):\$")
        val PatternInstruct = Pattern.compile("^([A-Z]{3})([ \\t]+(\\[)?([ \\t])?([_A-Za-z][_A-Za-z0-9]*)([ \\t])?(\\])*)?\$")
    }
}