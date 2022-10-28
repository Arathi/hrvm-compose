package com.undsf.hrvm.modules

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.components.TextBox
import com.undsf.hrvm.models.Assembler
import com.undsf.hrvm.models.Machine
import com.undsf.hrvm.models.Problem
import com.undsf.hrvm.views.buildMachine
import mu.KotlinLogging
import java.lang.Exception

private const val TabDescription = 0
private const val TabSourceCode = 1
private const val TabVisualProgram = 2
private const val TabInstructionTests = 3

private val logger = KotlinLogging.logger {}

@Composable
fun Debugger(problem: Problem, machine: Machine) {
    val assembler = Assembler()

    var tabIndex by remember { mutableStateOf(3) }
    var sources by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.width(400.dp),
    ) {
        Row {
            val spacerModifier = Modifier.width(10.dp)
            Spacer(spacerModifier)
            Button(onClick = {
                try {
                    val program = assembler.assemble(sources, true)
                    machine.loadProgram(program)
                    tabIndex = TabVisualProgram
                }
                catch (ex: Exception) {
                    logger.warn("编译出错！")
                }
            }) {
                Text("编译")
            }

            Spacer(spacerModifier)
            Button(onClick = {
                machine.processor.run()
            }) {
                Text("运行")
            }

            Spacer(spacerModifier)
            Button(onClick = {
                machine.processor.step()
            }) {
                Text("单步")
            }

            Spacer(spacerModifier)
            Button(onClick = {
                machine.reset()
            }) {
                Text("重置")
            }
        }

        TabRow(tabIndex) {
            Tab(
                tabIndex == TabDescription,
                onClick = { tabIndex = TabDescription },
                text = { Text("问题描述") }
            )
            Tab(
                tabIndex == TabSourceCode,
                onClick = { tabIndex = TabSourceCode },
                text = { Text("源代码") }
            )
            Tab(
                tabIndex == TabVisualProgram,
                onClick = { tabIndex = TabVisualProgram },
                text = { Text("可视化") }
            )
            Tab(
                tabIndex == TabInstructionTests,
                onClick = { tabIndex = TabInstructionTests },
                text = { Text("指令测试") }
            )
        }

        if (tabIndex == 0) {
            Text(problem.descriptions)
        }
        else if (tabIndex == 1) {
            TextBox(
                sources,
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(2.dp),
                onValueChange = { sources = it }
            )
        }
        else if (tabIndex == 2) {
            if (machine.program != null) {
                VisualProgram(machine.processor.pc, machine.program!!)
            }
            else {
                Text("程序未编译")
            }
        }
        else {
            InstructionTestModule(machine.processor)
        }
    }
}

@Composable
@Preview
private fun preview() {
    val problem = Problem.default
    val machine = buildMachine(problem)

    Box {
        Debugger(problem, machine)
    }
}
