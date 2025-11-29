package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.game.AIService
import com.example.myapplication.game.AVAILABLE_MODELS
import com.example.myapplication.game.GameSettings
import com.example.myapplication.game.HfModel
import com.example.myapplication.ui.components.DotMatrixText
import com.example.myapplication.ui.components.GlitchButton
import kotlinx.coroutines.launch

@Composable
fun ModelSelectionScreen(
    aiService: AIService,
    settings: GameSettings,
    onModelSelected: () -> Unit
) {
    var selectedModel by remember { mutableStateOf<HfModel?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DotMatrixText(text = "SELECT NEURAL CORE", fontSize = 24)
        Spacer(modifier = Modifier.height(24.dp))

        if (isDownloading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DotMatrixText(text = "DOWNLOADING...", fontSize = 20)
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { downloadProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color.Red,
                        trackColor = Color.DarkGray,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DotMatrixText(text = "${(downloadProgress * 100).toInt()}%", fontSize = 16)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AVAILABLE_MODELS) { model ->
                    ModelItem(
                        model = model,
                        isSelected = selectedModel == model,
                        onSelect = { selectedModel = model }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                DotMatrixText(text = errorMessage!!, color = Color.Red, fontSize = 12)
                Spacer(modifier = Modifier.height(8.dp))
            }

            GlitchButton(
                text = "INITIALIZE CORE",
                onClick = {
                    if (selectedModel != null) {
                        val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                        val file = java.io.File(downloadsDir, selectedModel!!.filename)
                        
                        if (file.exists() && file.length() > 0) {
                             settings.selectedModelName = selectedModel!!.filename
                             onModelSelected()
                        } else {
                            scope.launch {
                                isDownloading = true
                                errorMessage = null
                                try {
                                    aiService.downloadModelFromUrl(
                                        url = selectedModel!!.url,
                                        filename = selectedModel!!.filename,
                                        onProgress = { progress ->
                                            downloadProgress = progress
                                        }
                                    )
                                    settings.selectedModelName = selectedModel!!.filename
                                    onModelSelected()
                                } catch (e: Exception) {
                                    errorMessage = "DOWNLOAD FAILED: ${e.message}"
                                    isDownloading = false
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ModelItem(
    model: HfModel,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isSelected) Color.Red else Color.DarkGray)
            .background(if (isSelected) Color.Red.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            DotMatrixText(text = model.name, color = if (isSelected) Color.Red else Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            DotMatrixText(text = model.description, fontSize = 10, color = Color.Gray)
        }
        if (isSelected) {
            DotMatrixText(text = "[SELECTED]", color = Color.Red, fontSize = 10)
        }
    }
}
