package com.undsf.hrvm.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ComboBox(
    items: List<Any> = listOf(),
    selectedIndex: Int = -1,
    defaultText: String = "--- 请选择 ---",
    modifier: Modifier = Modifier,
    onSelectedIndexChange: (it: Int) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(selectedIndex) }

    Box {
        val value = if (selectedIndex in items.indices) {
            items[selectedIndex].toString()
        }
        else defaultText

        TextBox(value, onValueChange = {}, enabled = false, modifier = modifier.clickable {
            expanded = !expanded
        })

        DropdownMenu(expanded, onDismissRequest = {expanded = false}) {
            for (index in items.indices) {
                val item = items[index]
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    onSelectedIndexChange(selectedIndex)
                    expanded = false
                }) {
                    Text(item.toString())
                }
            }
        }
    }
}

@Composable
@Preview
private fun preview() {
    Column(modifier = Modifier.padding(10.dp)) {
        ComboBox(listOf("选项A", "选项B", "选项C"))
    }
}
