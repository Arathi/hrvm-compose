package com.undsf.hrvm.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.textButtonColors
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.models.*
import com.undsf.hrvm.models.Queue
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Composable
fun dataBlock(data: Data?) {
    val color = if (data != null) {
        if (data.type == DataType.INTEGER) Color(0xFF9CB65B)
        else if (data.type == DataType.CHARACTER) Color(0xFF8D8DC1)
        else Color.LightGray
    }
    else Color.LightGray

    Box(
        modifier = Modifier.size(32.dp, 32.dp)
            .background(color)
            .border(1.dp, Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        if (data != null) {
            val text = if (data.type == DataType.INTEGER) "${data.value}"
            else "${data.value.toChar()}"
            Text(text = text, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
@Preview
fun dataBlockPreview() {
    Column {
        val spacer = Modifier.height(10.dp)
        dataBlock(Data(1))
        Spacer(spacer)
        dataBlock(Data('A'))
        Spacer(spacer)
        dataBlock(null)
    }
}

@Composable
fun queue(title: String, queue: Queue) {
    val vSpacerModifier = Modifier.height(3.dp)
    Column(
        modifier = Modifier.width(48.dp)
                            .fillMaxHeight()
                            .background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title)
        Spacer(Modifier.height(10.dp))
        val datas: List<Data> = if (queue.type == QueueType.Outbox) queue.datas.reversed() else queue.datas
        for (data in datas) {
            dataBlock(data)
            Spacer(vSpacerModifier)
        }
    }
}

@Composable
@Preview
fun queuePreview() {
    val inbox = Queue(
        mutableListOf('A', 25, 'C', 36),
        QueueType.Inbox
    )
    val outbox = Queue(
        mutableListOf('X', -999),
        QueueType.Outbox
    )
    outbox.push('X')
    outbox.push(-999)

    Row {
        queue("输入", inbox)
        Spacer(Modifier.width(10.dp))
        queue("输出", outbox)
    }
}

@Composable
fun memory(memory: Memory) {
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(Color.LightGray),
        contentAlignment = Alignment.Center) {
        Column {
            val variableNames = memory.variableNames
            for (y in 0 until memory.height) {
                Row {
                    for (x in 0 until memory.width) {
                        val index = memory.getIndexByXY(x, y)
                        val data = memory.read(index)
                        val bgColor = if (x%2 != y%2) Color.Black else Color.White
                        val variableName = variableNames[index]

                        Box(modifier = Modifier.background(bgColor).size(64.dp)) {
                            if (data != null) {
                                Box(
                                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 3.dp)
                                ) {
                                    dataBlock(data)
                                }
                            }

                            if (variableName != null) {
                                Text(
                                    text = variableName,
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).align(Alignment.BottomStart),
                                    color = Color.Red,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Text(
                                text = index.toString(),
                                modifier = Modifier.align(Alignment.BottomEnd),
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun memoryPreview() {
    val mem0 = Memory(4, 4)
    mem0.registerVariable("white", 5)
    mem0.registerVariable("black", 9)
    mem0.registerVariable("ZERO", 15)
    mem0.write(5, -999)
    mem0.write(9, 'Z')
    mem0.write(6, 'R')
    mem0.write(10, 999)
    mem0.write(15, 0)
    memory(mem0)
}

@Composable
fun instTestModule(processor: Processor) {
    Column {
        val processorInfoName = Modifier.width(100.dp)
            // .background(Color.Gray)
            .align(Alignment.CenterHorizontally)
        val textFieldModifier = Modifier.width(140.dp).height(36.dp)

        var addr by remember { mutableStateOf(0) }
        var indirect by remember { mutableStateOf(false) }

        val colorQueue = Color(0xFF9CB65B)
        val colorMemory = Color(0xFFC96A54)
        val colorCompute = Color(0xFFC68D62)
        val colorJump = Color(0xFF8D8DC1)

        Spacer(Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Acc：", modifier = processorInfoName, textAlign = TextAlign.End)
            Text(processor.acc.toString())
        }

        Spacer(Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("PC：", modifier = processorInfoName, textAlign = TextAlign.End)
            Text(processor.pc.toString())
        }

        Spacer(Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("计数器：", modifier = processorInfoName, textAlign = TextAlign.End)
            Text(processor.counter.toString())
        }

        Spacer(Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("状态：", modifier = processorInfoName, textAlign = TextAlign.End)
            Text(processor.status.toString())
        }

        Spacer(Modifier.height(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("指令参数：", modifier = processorInfoName, textAlign = TextAlign.End)

            OutlinedTextField(addr.toString(), {
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
            }, colors = textButtonColors(
                backgroundColor = colorQueue,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("PLA", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.PHA)
            }, colors = textButtonColors(
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
            }, colors = textButtonColors(
                backgroundColor = colorMemory,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("LDA", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.STA(addr, indirect))
            }, colors = textButtonColors(
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
            }, colors = textButtonColors(
                backgroundColor = colorCompute,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("ADD", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.SUB(addr, indirect))
            }, colors = textButtonColors(
                backgroundColor = colorCompute,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("SUB", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.INC(addr, indirect))
            }, colors = textButtonColors(
                backgroundColor = colorCompute,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("INC", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.DEC(addr, indirect))
            }, colors = textButtonColors(
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
            }, colors = textButtonColors(
                backgroundColor = colorJump,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("JMP", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.BEQ(addr))
            }, colors = textButtonColors(
                backgroundColor = colorJump,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("BEQ", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(10.dp))
            Button(onClick = {
                processor.execute(Instruction.BMI(addr))
            }, colors = textButtonColors(
                backgroundColor = colorJump,
                contentColor = Color.Black
            ), modifier = instBtnModifier) {
                Text("BMI", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun debugger(problem: Problem, machine: Machine) {
    val assembler = Assembler()

    var tabIndex by remember { mutableStateOf(2) }
    var sources by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.width(400.dp),
    ) {
        Row {
            val spacerModifier = Modifier.width(10.dp)
            Spacer(spacerModifier)
            Button(onClick = {}) {
                Text("编译")
            }

            Spacer(spacerModifier)
            Button(onClick = {}) {
                Text("运行")
            }

            Spacer(spacerModifier)
            Button(onClick = {}) {
                Text("单步")
            }

            Spacer(spacerModifier)
            Button(onClick = {}) {
                Text("重置")
            }
        }

        TabRow(tabIndex) {
            Tab(
                tabIndex == 0,
                onClick = { tabIndex = 0 },
                text = { Text("问题描述") }
            )
            Tab(
                tabIndex == 1,
                onClick = { tabIndex = 1 },
                text = { Text("源代码") }
            )
            Tab(
                tabIndex == 2,
                onClick = { tabIndex = 2 },
                text = { Text("程序") }
            )
            Tab(
                tabIndex == 3,
                onClick = { tabIndex = 3 },
                text = { Text("指令测试") }
            )
        }

        if (tabIndex == 0) {
            Text(problem.descriptions)
        }
        else if (tabIndex == 1) {
            OutlinedTextField(
                sources,
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(2.dp),
                onValueChange = { sources = it }
            )
        }
        else if (tabIndex == 2) {

        }
        else {
            instTestModule(machine.processor)
        }
    }
}

@Composable
@Preview
fun debuggerPreview() {
    val problem = Problem(
        "测试问题描述",
        mutableListOf(1, 2, 'A', 'B', 'F', 'C', 4, 3),
        mutableListOf(2, 'B', 'F', 4),
        4,
        1
    )
    val machine = buildMachine(problem)

    Box {
        debugger(problem, machine)
    }
}

@Composable
fun program(pc: Int, program: Program) {
    Column {
        Text("指令数量：${program.length}")
        for (index in program.instructions.indices) {
            val inst = program.instructions[index]
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
    val program = hrasm.assemble(sources)

    program.instructions.add(Instruction.LDA(0))
    program.instructions.add(Instruction.NOP)
    program.instructions.add(Instruction.STA(1))
    program.instructions.add(Instruction.NOP)
    program.instructions.add(Instruction.LDA(2))
    program.instructions.add(Instruction.NOP)
    program.instructions.add(Instruction.SUB(1))
    program.instructions.add(Instruction.PHA)
    program(0, program)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainView(problem: Problem, windowSize: DpSize) {
    val machine by remember {
        mutableStateOf(
            buildMachine(problem)
        )
    }

    MaterialTheme {
        Row {
            // 输入队列
            queue("输入", machine.inbox)

            // 累加器、内存
            Column(modifier = Modifier.width(windowSize.width - (48*2+400).dp)) {
                // 累加器
                Box(modifier = Modifier.fillMaxWidth().height(64.dp),
                    contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("累加器：")
                        dataBlock(machine.processor.acc)
                    }
                }

                // 内存
                memory(machine.memory)
            }

            // 输出队列
            queue("输出", machine.outbox)

            // 调试器
            debugger(problem, machine)
        }

        if (machine.errorMessage != null) {
            fun onDialogClose() {
                // machine.errorMessage = null
                machine.reset()
            }
            AlertDialog(
                onDismissRequest = ::onDialogClose,
                modifier = Modifier.size(400.dp, 300.dp),
                title = { Text("警告") },
                text = {
                    if (machine.errorMessage != null) {
                        Text(machine.errorMessage!!)
                    }
                },
                confirmButton = {
                    Button(::onDialogClose) {
                        Text("确定")
                    }
                }
            )
        }
    }
}

fun buildMachine(problem: Problem) : Machine {
    return Machine(
        inboxSamples = problem.input,
        outboxSamples = problem.output,
        memoryWidth = problem.memoryWidth,
        memoryHeight = problem.memoryHeight
    )
}

@Composable
@Preview
fun MainViewPreview() {
    val problem = Problem(
        "测试问题描述",
        mutableListOf(1, 2, 'A', 'B', 'F', 'C', 4, 3),
        mutableListOf(2, 'B', 'F', 4),
        4,
        2
    )
    val windowSize = DpSize(1366.dp, 768.dp)
    MainView(problem, windowSize)
}
