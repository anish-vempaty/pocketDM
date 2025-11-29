package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.game.CharacterClass
import com.example.myapplication.game.Player
import com.example.myapplication.ui.components.DotMatrixText
import com.example.myapplication.ui.components.GlitchButton

@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (Player) -> Unit
) {
    var selectedClass by remember { mutableStateOf<CharacterClass?>(null) }
    var name by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DotMatrixText(text = "IDENTITY INITIALIZATION", fontSize = 24)
        Spacer(modifier = Modifier.height(24.dp))

        DotMatrixText(text = "SELECT CLASS:")
        Spacer(modifier = Modifier.height(8.dp))
        
        CharacterClass.values().forEach { charClass ->
            GlitchButton(
                text = charClass.className,
                onClick = { selectedClass = charClass },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedClass != null) {
            DotMatrixText(text = "SELECTED: ${selectedClass!!.className}")
            DotMatrixText(text = selectedClass!!.description)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            GlitchButton(
                text = "CONFIRM IDENTITY",
                onClick = {
                    val player = Player(
                        name = "Traveler", // Simplified for now
                        characterClass = selectedClass!!,
                        hp = selectedClass!!.baseHp,
                        maxHp = selectedClass!!.baseHp
                    )
                    onCharacterCreated(player)
                }
            )
        }
    }
}
