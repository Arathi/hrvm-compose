// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.undsf.hrvm.core.*
import com.undsf.hrvm.forms.MachineForm

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() = application {
    val processor = Processor(
        inbox = Inbox(1, 2, 15, 10, 'B', 'E', 6, 8),
        memory = Memory(6, 5),
        outbox = Outbox(2, 15, 'E', 8)
    )

    val machineForm = MachineForm(processor)
    val program = machineForm.hrasm.assemble("""
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
""".trimIndent())
    processor.load(program)

    Window(
        title = "Human Resource Virtual Machine",
        state = rememberWindowState(size = DpSize(1366.dp, 768.dp)),
        onCloseRequest = ::exitApplication) {
        machineForm.form()
    }
}
