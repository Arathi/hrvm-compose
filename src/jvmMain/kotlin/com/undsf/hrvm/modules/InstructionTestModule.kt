package com.undsf.hrvm.modules

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.undsf.hrvm.components.ComboBox
import com.undsf.hrvm.components.TextBox
import com.undsf.hrvm.models.Data
import com.undsf.hrvm.models.DataType
import com.undsf.hrvm.models.Instruction
import com.undsf.hrvm.models.Processor
import mu.KotlinLogging
import org.jetbrains.skia.impl.Stats.enabled
import java.lang.Exception

private val logger = KotlinLogging.logger {}

@Composable
fun InstructionTestModule(processor: Processor) {
    var addr by remember { mutableStateOf(0) }
    var indirect by remember { mutableStateOf(false) }
    var showAccEditor by remember { mutableStateOf(false) }

    Box {
        AccEditor(processor, showAccEditor) {
            showAccEditor = false
        }
    }

    Column {
        val processorInfoName = Modifier.width(100.dp)
            .align(Alignment.CenterHorizontally)

        val colorQueue = Color(0xFF9CB65B)
        val colorMemory = Color(0xFFC96A54)
        val colorCompute = Color(0xFFC68D62)
        val colorJump = Color(0xFF8D8DC1)

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(40.dp)) {
            Text("Acc：", modifier = processorInfoName, textAlign = TextAlign.End)
            Box(Modifier.clickable(enabled = true) {
                showAccEditor = true
            }) {
                DataBlock(processor.acc)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(40.dp)) {
            Text("PC：", modifier = processorInfoName, textAlign = TextAlign.End)
            Text(processor.pc.toString())
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(40.dp)) {
            Text("计数器：", modifier = processorInfoName, textAlign = TextAlign.End)
            Text(processor.counter.toString())
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(40.dp)) {
            Text("状态：", modifier = processorInfoName, textAlign = TextAlign.End)
            Text(processor.status.toString())
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(40.dp)) {
            Text("指令参数：", modifier = processorInfoName, textAlign = TextAlign.End)

            TextBox(addr.toString(), {
                try {
                    addr = it.toInt()
                }
                catch (ex: NumberFormatException) {
                    logger.warn { "数值转换失败" }
                }
            }, modifier = Modifier.width(100.dp))
            Spacer(Modifier.width(10.dp))

            Switch(indirect, {
                indirect = it
            })
            Spacer(Modifier.width(10.dp))

            Text( if (indirect) "间接寻址" else "绝对寻址" )
        }

        Spacer(Modifier.height(5.dp))

        val instBtnModifier = Modifier.width(70.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.PLA)
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorQueue,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("PLA", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.PHA)
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorQueue,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("PHA", fontWeight = FontWeight.Bold)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.LDA(addr, indirect))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorMemory,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("LDA", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.STA(addr, indirect))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorMemory,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("STA", fontWeight = FontWeight.Bold)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.ADD(addr, indirect))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorCompute,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("ADD", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.SUB(addr, indirect))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorCompute,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("SUB", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.INC(addr, indirect))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorCompute,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("INC", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.DEC(addr, indirect))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorCompute,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("DEC", fontWeight = FontWeight.Bold)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.JMP(addr))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorJump,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("JMP", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.BEQ(addr))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorJump,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("BEQ", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.BMI(addr))
            }, colors = ButtonDefaults.textButtonColors(
                backgroundColor = colorJump,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("BMI", fontWeight = FontWeight.Bold)
            }
        }
    }
}