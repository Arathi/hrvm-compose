package com.undsf.hrvm.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.core.*

const val TabSourceCode = 0
const val TabProgram = 1

val assembler = Assembler()

@Composable
fun dataBlock(data: Data?) {
    val color = if (data != null) {
        if (data.type == DataType.INTEGER) Color.Green
        else if (data.type == DataType.CHARACTER) Color.Blue
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
            Text(text = text, color = Color.Gray)
        }
    }
}

@Composable
@Preview
fun dataBlockPreview() {
    Column {
        dataBlock(Data(1))
        dataBlock(Data('B'))
        dataBlock(null)
    }
}

@Composable
fun queue(title: String, queue: DataQueue, desc: Boolean = false) {
    Column(
        modifier = Modifier.width(48.dp)
            .fillMaxHeight()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title)

        val datas = if (desc) queue.datas.reversed()
        else queue.datas
        for (data in datas) {
            dataBlock(data)
        }
    }
}

@Composable
@Preview
fun queuePreview() {
    Row {
        queue("输入", Inbox(1, 2, '3', '4', 5, 6))
        queue("输出", Inbox(2, '4', 6), true)
    }
}

@Composable
fun memory(mem: Memory) {
    Column(modifier = Modifier.border(1.dp, Color.Gray)) {
        for (y in 0 until mem.height) {
            Row {
                for (x in 0 until mem.width) {
                    val index = mem.getIndex(x, y)
                    val bgColor = if (x % 2 == y % 2) Color.White
                                    else Color.Black
                    val data = mem.getData(index)

                    Box(modifier = Modifier.size(64.dp, 64.dp).background(bgColor)) {
                        if (data != null) {
                            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                                dataBlock(data)
                            }
                        }

                        Text(
                            text = "$index",
                            modifier = Modifier.align(Alignment.BottomEnd),
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun memoryPreview() {
    val mem = Memory(6, 4, 0, '1', 2, null)
    memory(mem)
}

@Composable
fun MainView(processor: Processor) {
    var inbox by remember { mutableStateOf(processor.inbox) }
    var acc by remember { mutableStateOf(processor.acc) }
    var memory by remember { mutableStateOf(processor.memory) }
    var outbox by remember { mutableStateOf(processor.outbox) }
    var pc by remember { mutableStateOf(processor.pc) }

    var program by remember { mutableStateOf(processor.program) }
    var sources by remember { mutableStateOf(program.toString()) }

    var programTabIndex by remember { mutableStateOf(TabSourceCode) }

    MaterialTheme {

        fun update() {
            acc = processor.acc
            inbox = processor.inbox
            outbox = processor.outbox
        }

        Row {
            // INBOX
            Column {
                queue("输入", inbox)
            }

            // Acc + Memory
            Column {
                Row {
                    Text("累加器")
                    dataBlock(acc)
                }
                Row {
                    memory(memory)
                }
            }

            // OUTBOX
            Column {
                queue("输入", outbox, true)
            }

            // Program
            Column {
                Row {
                    Button(onClick = {

                    }) { Text("编译") }
                    Button(onClick = {

                    }) { Text("运行") }
                    Button(onClick = {

                    }) { Text("单步调试") }
                }

                TabRow(programTabIndex) {
                    Tab(TabSourceCode == programTabIndex, onClick = {
                        programTabIndex = TabSourceCode
                    })

                    Tab(TabProgram == programTabIndex, onClick = {
                        programTabIndex = TabProgram
                    })
                }

                when (programTabIndex) {
                    TabSourceCode -> {
                        OutlinedTextField(sources, onValueChange = {
                            sources = it
                        })
                    }
                    TabProgram -> {
                        val insts = program.instructs
                        Column(modifier = Modifier.border(1.dp, Color.Gray)) {
                            for (index in insts.indices) {
                                Row() {
                                    val inst = program.instructs[index]
                                    Column(modifier = Modifier.width(16.dp)) {
                                        if (pc == index) {
                                            Text(" > ")
                                        }
                                    }
                                    Column(modifier = Modifier.width(32.dp)) {
                                        Text("$index")
                                    }
                                    Column(modifier = Modifier.width(64.dp)) {
                                        Text(inst.label)
                                    }
                                    Column(modifier = Modifier.width(96.dp)) {
                                        Text(inst.toString(false, ""))
                                    }
                                }
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }
}

