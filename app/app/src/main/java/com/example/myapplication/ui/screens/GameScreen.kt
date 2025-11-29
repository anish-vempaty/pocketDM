package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.game.*
import com.example.myapplication.ui.components.DotMatrixText
import com.example.myapplication.ui.components.GlitchButton
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    player: Player,
    aiService: AIService,
    memoryManager: MemoryManager,
    settings: GameSettings
) {
    var input by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf<ChatMessage>() }
    val coroutineScope = rememberCoroutineScope()
    val diceRoller = remember { DiceRoller() }
    var lastRoll by remember { mutableStateOf(0) }
    
    // Game Session State with MutableState for observability
    class GameSessionState(initialPlayer: Player) {
        var player by mutableStateOf(initialPlayer)
        var location by mutableStateOf("Tavern")
        var summary by mutableStateOf("The adventure begins.")
        var money by mutableStateOf(initialPlayer.money)
        var npcs by mutableStateOf(mutableListOf<String>())
        var currentQuest by mutableStateOf("The Beginning")
        
        fun toGameSession(): GameSession {
            return GameSession(
                player = player.copy(money = money),
                location = location,
                summary = summary,
                npcs = npcs,
                currentQuest = currentQuest
            )
        }
        
        fun updateFromSession(session: GameSession) {
            this.player = session.player
            this.location = session.location
            this.summary = session.summary
            this.money = session.player.money
            this.npcs = session.npcs
            this.currentQuest = session.currentQuest
        }
    }

    val gameSessionState = remember { GameSessionState(player) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val currentGeneration by aiService.currentGeneration.collectAsState()
    val isGenerating by aiService.isGenerating.collectAsState()

    // Auto-scroll when chat history changes or streaming updates
    LaunchedEffect(chatHistory.size, currentGeneration, isGenerating) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size + (if (currentGeneration.isNotEmpty() || isGenerating) 1 else 0) - 1)
        }
    }

    // Initial DM Message
    LaunchedEffect(Unit) {
        if (chatHistory.isEmpty()) {
            coroutineScope.launch {
                kotlinx.coroutines.delay(500)
                val session = gameSessionState.toGameSession()
                val introPrompt = "Start the adventure for a Level 1 ${player.characterClass.className} named ${player.name}. Describe the current location: ${session.location}."
                val response = aiService.getDMResponse(introPrompt, session, settings, memoryManager)
                
                // Update UI State
                gameSessionState.updateFromSession(session)
                
                chatHistory.add(ChatMessage("DM", response))
                memoryManager.addLore("Start", response)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                DotMatrixText(text = "HP: ${gameSessionState.player.hp}/${gameSessionState.player.maxHp}")
                DotMatrixText(text = "LVL: ${gameSessionState.player.level}")
                DotMatrixText(text = "GOLD: ${gameSessionState.money}")
            }
            Column(horizontalAlignment = Alignment.End) {
                DotMatrixText(text = "LOC: ${gameSessionState.location}", color = Color.Green)
                DotMatrixText(text = "MODE: ${if (settings.isOfflineMode) "OFFLINE" else "HYBRID"}", color = Color.Gray)
            }
        }
        
        // Summary Display
        if (gameSessionState.summary.isNotEmpty()) {
            DotMatrixText(text = "SUMMARY: ${gameSessionState.summary.take(50)}...", fontSize = 10, color = Color.Gray)
        }
        
        // Chat Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(1.dp, Color.DarkGray)
                .background(Color(0xFF050505))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(chatHistory) { message ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DotMatrixText(
                                text = "[${message.sender}]", 
                                color = if (message.sender == "Player") Color.Cyan else Color.Red,
                                fontSize = 12
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DotMatrixText(
                                text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(message.timestamp),
                                color = Color.DarkGray,
                                fontSize = 10
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        DotMatrixText(text = message.content, lineHeight = 18.sp)
                    }
                    androidx.compose.material3.HorizontalDivider(color = Color(0xFF222222), thickness = 1.dp)
                }
                
                // Streaming Indicator
                if (currentGeneration.isNotEmpty() || isGenerating) {
                    item {
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            DotMatrixText(text = "[DM] (Thinking...)", color = Color.Red, fontSize = 12)
                        }
                    }
                }
            }
        }

        // Dice Roll Display
        if (lastRoll > 0) {
            DotMatrixText(text = "LAST ROLL: $lastRoll", color = Color.Yellow)
        }

        // Input Area
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                placeholder = { DotMatrixText(text = "Type action...", color = Color.Gray) }
            )
            
            GlitchButton(text = "ACT", onClick = {
                if (input.isNotBlank()) {
                    val userMsg = input
                    // Auto-roll
                    lastRoll = diceRoller.roll(20)
                    val msgWithRoll = "$userMsg (Rolled: $lastRoll)"
                    
                    chatHistory.add(ChatMessage("Player", msgWithRoll))
                    input = ""
                    
                    coroutineScope.launch {
                        if (userMsg.startsWith("/c")) {
                            val response = aiService.getCompanionResponse(userMsg.removePrefix("/c"), gameSessionState.summary, settings)
                            chatHistory.add(ChatMessage("Companion", response))
                        } else {
                            val session = gameSessionState.toGameSession()
                            val response = aiService.getDMResponse(msgWithRoll, session, settings, memoryManager)
                            
                            // Update UI State
                            gameSessionState.updateFromSession(session)
                            
                            chatHistory.add(ChatMessage("DM", response))
                            memoryManager.addLore("Turn", response)
                        }
                    }
                }
            })
        }
    }
}
