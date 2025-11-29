package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.DotMatrixText
import com.example.myapplication.ui.components.GlitchButton
import kotlin.system.exitProcess

@Composable
fun MainMenuScreen(
    onStartGame: () -> Unit,
    onOptions: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DotMatrixText(text = "MAIN MENU", fontSize = 24)
        Spacer(modifier = Modifier.height(32.dp))
        
        GlitchButton(text = "START GAME", onClick = onStartGame)
        Spacer(modifier = Modifier.height(16.dp))
        
        GlitchButton(text = "OPTIONS", onClick = onOptions)
        Spacer(modifier = Modifier.height(16.dp))
        
        GlitchButton(
            text = "DISCOVERY MODE (SDK)",
            onClick = { onNavigate("discovery") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        GlitchButton(text = "EXIT GAME", onClick = { exitProcess(0) })
    }
}
