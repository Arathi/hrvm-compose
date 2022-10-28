package com.undsf.hrvm.modules

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.models.Data
import com.undsf.hrvm.models.Queue
import com.undsf.hrvm.models.QueueType

@Composable
fun QueueModule(title: String, queue: Queue) {
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
            DataBlock(data)
            Spacer(vSpacerModifier)
        }
    }
}

@Composable
@Preview
private fun preview() {
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
        QueueModule("输入", inbox)
        Spacer(Modifier.width(10.dp))
        QueueModule("输出", outbox)
    }
}
