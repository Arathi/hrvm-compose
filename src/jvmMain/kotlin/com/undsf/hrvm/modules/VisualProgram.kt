package com.undsf.hrvm.modules

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.models.*

@Composable
fun VisualProgram(pc: Int, program: Program) {
    val scrollState = rememberScrollState(0)

    Column(modifier = Modifier.fillMaxWidth()
        .fillMaxHeight()
        .background(Color(0xFFBB9F8A))
        .verticalScroll(scrollState)
    ) {
        val instModifier = Modifier

        // Text("指令数量：${program.length}")
        Spacer(Modifier.height(10.dp))

        var counter = 0
        for (index in program.instructions.indices) {
            when (val inst = program.instructions[index]) {
                is VisualInstruction -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(10.dp))
                        // 箭头
                        if (pc == index) {
                            Text(
                                "►",
                                modifier = Modifier.width(16.dp),
                                color = Color.Green
                            )
                        }
                        else {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        Spacer(modifier = Modifier.width(5.dp))

                        // 序号
                        if (inst.executable) {
                            counter++
                            Text(
                                text = counter.toString(),
                                modifier = Modifier.width(20.dp),
                                textAlign = TextAlign.End,
                                color = Color(0xFF776557),
                            )
                        }

                        // 指令
                        if (inst.isLabel) {
                            Box(modifier = Modifier.background(inst.color).padding(8.dp)) {
                                Text(inst.label + ":", fontWeight = FontWeight.Bold)
                            }
                        }
                        else {
                            Spacer(Modifier.width(16.dp))
                            Box(modifier = Modifier.background(inst.color).padding(8.dp)) {
                                Text(inst.operName, fontWeight = FontWeight.Bold)
                            }
                        }

                        // 参数
                        if (inst.operData != null) {
                            Spacer(Modifier.width(10.dp))
                            Box(modifier = Modifier.background(inst.color).padding(8.dp)) {
                                when (inst.operData) {
                                    is Int -> {
                                        Text(
                                            inst.operData.toString(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    is String -> {
                                        Text(
                                            text = inst.operData.toString(),
                                            modifier = Modifier.background(Color.White)
                                                                .padding(start = 3.dp, end = 3.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
                else -> {
                    Row {
                        Text(
                            if (index == pc) ">" else " ",
                            modifier = Modifier.width(16.dp),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            inst.toString(),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun programPreview() {
    val sources = """
.set first 0
.set second 1

start:
    PLA
    STA first
    PLA
    STA second

compare:
    SUB first
    BMI loadSecond

loadFirst:
    LDA first
    JMP output

loadSecond:
    LDA second

output:
    PHA
    JMP start
""".trimIndent()

    val hrasm = Assembler()
    val program = hrasm.assemble(sources, visual = true)

    // program.instructions.add(Instruction.LDA(0))
    // program.instructions.add(Instruction.NOP)
    // program.instructions.add(Instruction.STA(1))
    // program.instructions.add(Instruction.NOP)
    // program.instructions.add(Instruction.LDA(2))
    // program.instructions.add(Instruction.NOP)
    // program.instructions.add(Instruction.SUB(1))
    // program.instructions.add(Instruction.PHA)
    VisualProgram(0, program)
}
