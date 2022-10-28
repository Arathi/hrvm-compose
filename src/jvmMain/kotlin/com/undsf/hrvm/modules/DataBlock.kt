package com.undsf.hrvm.modules

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.undsf.hrvm.models.Data
import com.undsf.hrvm.models.DataType

@Composable
fun DataBlock(data: Data?) {
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
private fun preview() {
    Column(modifier = Modifier.padding(10.dp)) {
        val spacer = Modifier.height(10.dp)
        DataBlock(Data(1))
        Spacer(spacer)
        DataBlock(Data('A'))
        Spacer(spacer)
        DataBlock(null)
        Spacer(spacer)
        DataBlock(Data(999))
        Spacer(spacer)
        DataBlock(Data(-999))
    }
}
