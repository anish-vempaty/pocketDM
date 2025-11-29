package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cactus.CactusModel
import com.example.myapplication.game.AIService
import com.example.myapplication.ui.components.DotMatrixText
import com.example.myapplication.ui.components.GlitchButton
import kotlinx.coroutines.launch

@Composable
fun DiscoveryScreen(
    aiService: AIService,
    settings: com.example.myapplication.game.GameSettings,
    onBack: () -> Unit
) {
    var models by remember { mutableStateOf<List<CactusModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var statusMessage by remember { mutableStateOf("Fetching models...") }
    val scope = rememberCoroutineScope()
    val initStatus by aiService.initStatus.collectAsState()
    val initError by aiService.initError.collectAsState()

    LaunchedEffect(Unit) {
        try {
            models = aiService.getAvailableModels()
            isLoading = false
        } catch (e: Exception) {
            statusMessage = "Error: ${e.message}"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DotMatrixText(text = "SDK DISCOVERY MODE", fontSize = 24)
        Spacer(modifier = Modifier.height(16.dp))

        if (initError != null) {
            DotMatrixText(text = "ERROR: $initError", color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        DotMatrixText(text = "STATUS: $initStatus", color = Color.Green)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            DotMatrixText(text = statusMessage)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(models) { model ->
                    CactusModelItem(model = model) {
                        scope.launch {
                            aiService.downloadAndInitSDKModel(model.slug)
                            settings.selectedModelName = model.slug
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        GlitchButton(text = "BACK", onClick = onBack)
    }
}

@Composable
fun CactusModelItem(
    model: CactusModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.DarkGray)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            DotMatrixText(text = model.name, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            DotMatrixText(text = "Slug: ${model.slug}", fontSize = 10, color = Color.Gray)
            DotMatrixText(text = "Size: ${model.size_mb} MB", fontSize = 10, color = Color.Gray)
            DotMatrixText(text = "Downloaded: ${model.isDownloaded}", fontSize = 10, color = if (model.isDownloaded) Color.Green else Color.Gray)
        }
        DotMatrixText(text = "LOAD", color = Color.Red)
    }
}
