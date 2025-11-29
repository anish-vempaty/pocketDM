package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.game.ApiRole
import com.example.myapplication.game.GameSettings
import com.example.myapplication.ui.components.DotMatrixText
import com.example.myapplication.ui.components.GlitchButton

@Composable
fun OptionsScreen(
    settings: GameSettings,
    onBack: () -> Unit
) {
    var isOffline by remember { mutableStateOf(settings.isOfflineMode) }
    var isCompanionEnabled by remember { mutableStateOf(settings.isCompanionEnabled) }
    var apiRole by remember { mutableStateOf(settings.apiRole) }
    var apiKey by remember { mutableStateOf(settings.geminiApiKey) }
    var useGpu by remember { mutableStateOf(settings.useGpu) }

    // Update settings object when local state changes
    LaunchedEffect(isOffline, isCompanionEnabled, apiRole, apiKey, useGpu) {
        settings.isOfflineMode = isOffline
        settings.isCompanionEnabled = isCompanionEnabled
        settings.apiRole = apiRole
        settings.geminiApiKey = apiKey
        settings.useGpu = useGpu
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            DotMatrixText(text = "SYSTEM CONFIG", fontSize = 24)
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // Settings List
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // AI Mode Section
            SettingSection("AI PROCESSING") {
                SettingRow("MODE") {
                    GlitchButton(
                        text = if (isOffline) "OFFLINE" else "HYBRID",
                        onClick = { isOffline = !isOffline }
                    )
                }
                
                if (!isOffline) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DotMatrixText(text = "GEMINI API KEY:", fontSize = 12, color = Color.Gray)
                    androidx.compose.material3.TextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.DarkGray),
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedContainerColor = Color.Black,
                            unfocusedContainerColor = Color.Black,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.Gray,
                            cursorColor = Color.Red
                        ),
                        placeholder = { DotMatrixText(text = "Paste Key Here...", color = Color.DarkGray) },
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingRow("CLOUD ROLE") {
                        GlitchButton(
                            text = apiRole.name,
                            onClick = { 
                                apiRole = if (apiRole == ApiRole.DM) ApiRole.COMPANION else ApiRole.DM 
                            }
                        )
                    }
                }
            }

            // Hardware Section
            SettingSection("HARDWARE") {
                SettingRow("ACCELERATION") {
                    GlitchButton(
                        text = if (useGpu) "CPU + GPU" else "CPU ONLY",
                        onClick = { useGpu = !useGpu }
                    )
                }
            }

            // Gameplay Section
            SettingSection("GAMEPLAY") {
                SettingRow("COMPANION") {
                    GlitchButton(
                        text = if (isCompanionEnabled) "ACTIVE" else "DISABLED",
                        onClick = { isCompanionEnabled = !isCompanionEnabled }
                    )
                }
            }
        }

        GlitchButton(text = "APPLY & BACK", onClick = onBack)
    }
}

@Composable
fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DotMatrixText(text = "[$title]", color = Color.Red, fontSize = 14)
        Spacer(modifier = Modifier.height(12.dp))
        Column(modifier = Modifier.padding(start = 8.dp)) {
            content()
        }
        Spacer(modifier = Modifier.height(12.dp))
        androidx.compose.material3.HorizontalDivider(color = Color.DarkGray, thickness = 1.dp)
    }
}

@Composable
fun SettingRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DotMatrixText(text = label, color = Color.White)
        content()
    }
}
