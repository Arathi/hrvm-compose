package com.undsf.hrvm.models

import com.undsf.hrvm.exceptions.AssemblyException
import mu.KotlinLogging
import java.util.regex.Matcher
import java.util.regex.Pattern

private val logger = KotlinLogging.logger {}

/**
 * 汇编器
 */
class Assembler {
    val variables = mutableMapOf<String, Int>()
    val labels = mutableMapOf<String, Int>()
    val interInsts = mutableListOf<IntermediateInstruction>()
    var lastLabels = mutableSetOf<String>()

    fun assemble(sources: String) : Program {
        variables.clear()
        labels.clear()
        interInsts.clear()

        val program = Program()
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
                interInsts.add(inst)
            }
        }

        for (index in interInsts.indices) {
            val inst = interInsts[index]
            if (inst.label != null) {
                labels[inst.label!!] = index
            }
        }

        for (inst in interInsts) {
            when (inst.oper) {
                "LDA", "STA",
                "ADD", "SUB",
                "INC", "DEC" -> {
                    if (inst.data == null && inst.param != null) {
                        if (variables.containsKey(inst.param)) {
                            inst.data = variables[inst.param]
                        }
                        else {
                            throw AssemblyException(inst.lineNo!!, inst.source!!, "变量${inst.param}不存在")
                        }
                    }
                }
                "JMP", "BEQ", "BMI" -> {
                    if (inst.data == null && inst.param != null) {
                        if (labels.containsKey(inst.param)) {
                            inst.data = labels[inst.param]
                        }
                        else {
                            throw AssemblyException(inst.lineNo!!, inst.source!!, "标签${inst.param}不存在")
                        }
                    }
                }
                else -> {}
            }
            program.addInstruction(inst.toInstruction())
        }

        logger.info { "汇编完成" }
        return program
    }

    fun assembleLine(lineNo: Int, source: String) : IntermediateInstruction? {
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
            lastLabels.add(label)
            return IntermediateInstruction.Label(label)
        }

        matcher = PatternInstruct.matcher(source)
        if (matcher.find()) {
            val opCodeStr = matcher.group(1)
            val valueStr = matcher.group(5)
            var counter = 0
            if (matcher.group(3) == "(") counter++
            if (matcher.group(7) == ")") counter++
            if (counter == 1) {
                throw AssemblyException(lineNo, source, "检测到不对称的括号")
            }
            val indirect = counter == 2
            val inst = buildInstruct(opCodeStr, valueStr, indirect) ?: throw AssemblyException(lineNo, source, "无法识别的汇编语句")
            inst.lineNo = lineNo
            inst.source = source
            // lastLabels = mutableSetOf()
            return inst
        }

        throw AssemblyException(lineNo, source, "无法识别的汇编语句")
    }

    fun buildInstruct(opCodeStr: String, valueStr: String? = null, indirect: Boolean = false) : IntermediateInstruction? {
        val inst = IntermediateInstruction(opCodeStr, indirect = indirect)

        if (valueStr != null) {
            try {
                inst.data = valueStr.toInt()
            }
            catch (ex: NumberFormatException) {
                inst.param = valueStr
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