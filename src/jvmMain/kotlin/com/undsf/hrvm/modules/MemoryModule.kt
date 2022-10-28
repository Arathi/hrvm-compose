package com.undsf.hrvm.modules

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.models.Memory

@Composable
fun MemoryModule(memory: Memory) {
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
                                    DataBlock(data)
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
private fun preview() {
    val mem0 = Memory(4, 4)
    mem0.registerVariable("white", 5)
    mem0.registerVariable("black", 9)
    mem0.registerVariable("ZERO", 15)
    mem0.write(5, -999)
    mem0.write(9, 'Z')
    mem0.write(6, 'R')
    mem0.write(10, 999)
    mem0.write(15, 0)
    MemoryModule(mem0)
}