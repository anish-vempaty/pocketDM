package com.example.myapplication.game

data class HfModel(
    val name: String,
    val filename: String,
    val url: String,
    val description: String
)

val AVAILABLE_MODELS = listOf(
    HfModel(
        name = "Gemma 3 1B (Official)",
        filename = "gemma-3-1b-it-Q4_K_M.gguf",
        url = "https://huggingface.co/Cactus-Compute/Gemma3-1B-Instruct-GGUF/resolve/main/gemma-3-1b-it-Q4_K_M.gguf",
        description = "Official Cactus version. Best compatibility."
    ),
    HfModel(
        name = "Qwen 3 0.6B (Official)",
        filename = "Qwen3-0.6B-Q8_0.gguf",
        url = "https://huggingface.co/Cactus-Compute/Qwen3-600m-Instruct-GGUF/resolve/main/Qwen3-0.6B-Q8_0.gguf",
        description = "Official Cactus version. Very fast."
    ),
    HfModel(
        name = "TinyLlama 1.1B",
        filename = "tinyllama-1.1b-chat-v1.0.q4_k_m.gguf",
        url = "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.q4_k_m.gguf",
        description = "Older, highly compatible model. Use if others fail."
    )
)
