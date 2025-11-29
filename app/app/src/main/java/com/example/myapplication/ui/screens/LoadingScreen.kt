package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.game.AIService
import com.example.myapplication.game.GameSettings
import com.example.myapplication.ui.components.DotMatrixText
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    aiService: AIService,
    settings: GameSettings,
    onModelReady: () -> Unit,
    onNavigateToModelSelection: () -> Unit
) {
    val isModelReady by aiService.isModelReady.collectAsState()
    val initStatus by aiService.initStatus.collectAsState()
    val initError by aiService.initError.collectAsState()
    
    LaunchedEffect(Unit) {
        if (settings.selectedModelName == null) {
            onNavigateToModelSelection()
        } else {
            aiService.initialize(settings.selectedModelName)
        }
    }
    
    LaunchedEffect(isModelReady) {
        if (isModelReady) {
            delay(1000)
            onModelReady()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DotMatrixText(text = "INITIALIZING...", fontSize = 24)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (initError != null) {
            DotMatrixText(text = "ERROR:", color = Color.Red)
            DotMatrixText(text = initError!!, color = Color.Red, fontSize = 12)
        } else if (isModelReady) {
            DotMatrixText(text = "SYSTEM READY", color = Color.Green)
        } else {
            DotMatrixText(text = initStatus, color = Color.Gray)
            DotMatrixText(text = "This may take a few minutes.", color = Color.Gray, fontSize = 12)
        }
    }
}
