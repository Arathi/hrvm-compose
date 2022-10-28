package com.undsf.hrvm.modules

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.undsf.hrvm.components.ComboBox
import com.undsf.hrvm.components.TextBox
import com.undsf.hrvm.models.Data
import com.undsf.hrvm.models.DataType
import com.undsf.hrvm.models.Processor
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val TYPE_NULL = 0
private const val TYPE_INTEGER = 1
private const val TYPE_CHARACTER = 2

@Composable
fun AccEditor(
    processor: Processor,
    visible: Boolean = false,
    onCloseRequest: () -> Unit = {
        logger.warn("对话框关闭事件未定义")
    }
) {
    val initAcc = processor.acc
    val itemNameModifier = Modifier.width(80.dp)
    var type by remember { mutableStateOf(
        if (initAcc != null) {
            when (initAcc.type) {
                DataType.INTEGER -> TYPE_INTEGER
                DataType.CHARACTER -> TYPE_CHARACTER
            }
        }
        else TYPE_NULL
    ) }
    var value: String by remember { mutableStateOf(
        initAcc?.value?.toString() ?: ""
    ) }

    fun buildData() : Data? {
        val data: Data? = when (type) {
            TYPE_INTEGER -> {
                try {
                    Data(value.toInt())
                }
                catch (ex: NumberFormatException) {
                    logger.warn { "数值转换失败：$value" }
                    null
                }
            }
            TYPE_CHARACTER -> {
                if (value.isNotEmpty()) {
                    val ch: Char = value[0]
                    Data(ch)
                }
                else null
            }
            else -> null
        }
        return data
    }

    fun onSave() {
        processor.acc = buildData()
    }

    Dialog(
        onCloseRequest = onCloseRequest,
        visible = visible,
        title = "编辑累加器",
        resizable = false) {

        Column(modifier = Modifier.padding(10.dp)) {
            Row {
                Text("类型", itemNameModifier)
                ComboBox(listOf("空值", "整数", "字符")) {
                    type = it
                }
            }
            Spacer(Modifier.height(10.dp))

            Row {
                Text("值", itemNameModifier)
                TextBox(value, { value = it })
            }
            Spacer(Modifier.height(10.dp))

            Row {
                Text("预览", itemNameModifier)
                DataBlock(buildData())
            }
            Spacer(Modifier.height(10.dp))

            Row {
                Button({
                    onSave()
                    onCloseRequest()
                }) {
                    Text("保存")
                }

                Spacer(Modifier.width(10.dp))

                Button({
                    onCloseRequest()
                }) {
                    Text("关闭")
                }
            }
        }
    }
}
