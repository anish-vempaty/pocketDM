package com.example.myapplication

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DotMatrixScreen() {
    var inputText by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    val llmManager = remember { LLMManager() }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter your prompt") }
        )
        Button(
            onClick = { 
                llmManager.getResponse(inputText) { response ->
                    responseText = response
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Submit")
        }
        if (responseText.isNotEmpty()) {
            Text(
                text = responseText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
