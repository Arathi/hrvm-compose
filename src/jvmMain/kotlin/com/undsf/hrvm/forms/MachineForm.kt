package com.undsf.hrvm.forms

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
import com.undsf.hrvm.core.exceptions.RuntimeException

class MachineForm(val processor: Processor) {
    val hrasm = Assembler()

    @Composable
    @Preview
    fun dataBlock(data: Data) {
        val color = when(data.type) {
            DataType.INTEGER -> Color.Green  // Color(0x8E8FC1)
            else -> Color.Blue // Color(0xA1CC5F)
        }
        val value: String = when(data.type) {
            DataType.CHARACTER -> "${data.value.toChar()}"
            else -> "${data.value}"
        }
        Box(modifier = Modifier.size(32.dp, 32.dp).background(color).border(1.dp, color = Color.Gray),
            contentAlignment = Alignment.Center) {
            Text(color = Color.Gray, text = value)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    @Preview
    fun form() {
        var acc by remember { mutableStateOf( processor.acc ) }
        val inbox by remember { mutableStateOf( processor.inbox ) }
        val outbox by remember { mutableStateOf( processor.outbox ) }
        val memory by remember { mutableStateOf( processor.memory ) }
        var program by remember { mutableStateOf( processor.program ) }

        @Composable
        @Preview
        fun queue(title: String, dataQueue: DataQueue) {
            val queue by remember { mutableStateOf(dataQueue) }

            Box(modifier = Modifier.width(64.dp).height(640.dp).background(Color.LightGray),
                contentAlignment = Alignment.TopCenter) {
                Column {
                    Text(title)

                    for (index in 0 until queue.size) {
                        val data = queue[index]
                        dataBlock(data)
                    }
                }
            }
        }

        @Composable
        @Preview
        fun acc() {
            Row(modifier = Modifier.height(64.dp)) {
                Text("累加器：")
                if (acc != null) {
                    dataBlock(acc!!)
                }
            }
        }

        @Composable
        @Preview
        fun memory() {
            val memory by remember { mutableStateOf(processor.memory) }

            Column {
                for (y in 0 until memory.height) {
                    Row {
                        for (x in 0 until memory.width) {
                            val color: Color = when {
                                x % 2 == y % 2 -> Color.White
                                else -> Color.Black
                            }

                            Box(modifier = Modifier.size(64.dp, 64.dp)
                                .background(color)
                                .border(1.dp, Color.Gray)) {
                                val index = memory.getIndex(x, y)
                                val data = memory.getData(index)
                                if (data != null) {
                                    dataBlock(data)
                                }
                                Text(
                                    modifier = Modifier.align(Alignment.BottomEnd),
                                    color = Color.Gray,
                                    text = "$index"
                                )
                            }
                        }
                    }
                }
            }
        }

        @Composable
        @Preview
        fun program(program: Program) {
            var sources by remember { mutableStateOf(program.toString()) }
            var selectedTabIndex by remember { mutableStateOf(0) }
            var showAlertDialog by remember { mutableStateOf(false) }

            if (showAlertDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showAlertDialog = false
                    },
                    title = {
                        Text("警告")
                    },
                    text = {
                        "编译出错"
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showAlertDialog = false
                        }) {
                            Text("关闭")
                        }
                    }
                )
            }

            Column {
                // 按钮
                Row(modifier = Modifier.height(32.dp)) {
                    Button(onClick = {
                        try {
                            val prg = hrasm.assemble(sources)
                            processor.load(prg)
                            selectedTabIndex = 1
                        }
                        catch (ex: RuntimeException) {
                        }
                    }) {
                        Text("编译")
                    }

                    Button(onClick = {
                        processor.run()
                    }) {
                        Text("运行")
                    }

                    Button(onClick = {
                        processor.step()
                    }) {
                        Text("单步执行")
                    }
                }

                TabRow(selectedTabIndex) {
                    Tab(
                        selected = 0 == selectedTabIndex,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("源码") }
                    )
                    Tab(
                        selected = 1 == selectedTabIndex,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("指令") }
                    )
                }

                if (selectedTabIndex == 0) {
                    OutlinedTextField(
                        value = sources,
                        singleLine = false,
                        onValueChange = {
                            sources = it
                        },
                        modifier = Modifier.fillMaxWidth().fillMaxHeight()
                    )
                }
                else if (selectedTabIndex == 1) {
                    // 表头
                    Row {
                        Box(modifier = Modifier.width(16.dp)) {
                            Text("")
                        }
                        Box(modifier = Modifier.width(48.dp)) {
                            Text("序号")
                        }
                        Box(modifier = Modifier.width(128.dp)) {
                            Text("标签")
                        }
                        Box {
                            Text("指令")
                        }
                    }

                    for (index in 0 until program.size) {
                        val inst = program[index]
                        Row {
                            Box(modifier = Modifier.width(16.dp)) {
                                if (processor.pc == index) {
                                    Text(">")
                                }
                            }
                            Box(modifier = Modifier.width(48.dp)) {
                                Text("$index")
                            }
                            Box(modifier = Modifier.width(128.dp)) {
                                var label = ""
                                if (inst.labels != null && inst.labels?.size!! > 0) {
                                    label = inst.labels!!.first()
                                }
                                Text(label)
                            }
                            Box {
                                Text(inst.toString(false, ""))
                            }
                        }
                    }
                }
            }
        }

        val processor by remember { mutableStateOf(this.processor) }

        MaterialTheme {
            Row {
                queue("输入", processor.inbox)
                Column(modifier = Modifier.width(420.dp)) {
                    acc()
                    memory()
                }
                queue("输出", processor.outbox)
                program(processor.program)
            }
        }
    }

    fun asm() {
        val sources = """
.set first 0
.set second 1
start:
    JMP input
getFirst:
    LDA first
output:
    PHA
input:
    PLA
    STA first
    PLA
    STA second
    SUB first
    BMI getFirst
getSecond:
    LDA second
    JMP output
""".trimIndent()
        hrasm.assemble(sources)
    }
}