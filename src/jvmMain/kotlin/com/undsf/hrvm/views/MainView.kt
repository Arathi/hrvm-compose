package com.undsf.hrvm.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.models.*
import com.undsf.hrvm.modules.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

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
            QueueModule("输入", machine.inbox)

            // 累加器、内存
            Column(modifier = Modifier.width(windowSize.width - (48*2+400).dp)) {
                // 累加器
                Box(modifier = Modifier.fillMaxWidth().height(64.dp),
                    contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("累加器：")
                        DataBlock(machine.processor.acc)
                    }
                }

                // 内存
                MemoryModule(machine.memory)
            }

            // 输出队列
            QueueModule("输出", machine.outbox)

            // 调试器
            Debugger(problem, machine)
        }

        if (machine.errorMessage != null) {
            fun onDialogClose() {
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
    val problem = Problem.Problems[2]
    val windowSize = DpSize(1366.dp, 768.dp)
    MainView(problem, windowSize)
}
