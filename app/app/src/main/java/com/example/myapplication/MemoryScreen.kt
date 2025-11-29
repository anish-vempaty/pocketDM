package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val memory = mutableMapOf<String, String>()

@Composable
fun MemoryScreen() {
    var key by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = key,
                onValueChange = { key = it },
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                label = { Text("Key") }
            )
            TextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.weight(1f),
                label = { Text("Value") }
            )
        }
        Button(
            onClick = { 
                if (key.isNotEmpty() && value.isNotEmpty()) {
                    memory[key] = value
                    key = ""
                    value = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add to Memory")
        }
        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            items(memory.toList()) { (key, value) ->
                Text("$key: $value")
            }
        }
    }
}
