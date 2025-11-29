package com.example.myapplication

import com.cactus.CactusLM
import com.cactus.CactusInitParams
import com.cactus.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LLMManager {
    private val lm = CactusLM()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            lm.downloadModel(model = "qwen3-0.6")
            lm.initializeModel(CactusInitParams())
        }
    }

    fun getResponse(prompt: String, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = lm.generateCompletion(
                messages = listOf(ChatMessage(content = prompt, role = "user"))
            )
            callback(result?.response ?: "Error: No response from model")
        }
    }
}
