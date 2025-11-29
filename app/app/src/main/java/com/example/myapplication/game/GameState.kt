package com.example.myapplication.game

import kotlinx.serialization.Serializable

enum class CharacterClass(val className: String, val description: String, val baseHp: Int) {
    WARRIOR("Warrior", "A master of martial combat.", 12),
    MAGE("Mage", "A wielder of arcane magic.", 6),
    ROGUE("Rogue", "A scoundrel who uses stealth and trickery.", 8),
    CLERIC("Cleric", "A priestly champion who wields divine magic.", 10)
}

@Serializable
data class Player(
    val name: String,
    val characterClass: CharacterClass,
    var hp: Int,
    var maxHp: Int,
    var level: Int = 1,
    var xp: Int = 0,
    var inventory: MutableList<String> = mutableListOf(),
    var money: String = "0 gold" // Added money
)

data class ChatMessage(
    val sender: String, // "Player", "DM", "Companion"
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class GameSession(
    val player: Player,
    val chatHistory: MutableList<ChatMessage> = mutableListOf(),
    var currentQuest: String = "The Beginning",
    var location: String = "Tavern",
    var npcs: MutableList<String> = mutableListOf(),
    var summary: String = "The adventure begins."
)

@Serializable
data class PlayerJson(
    val `class`: String,
    val hp: Int,
    val money: String
)

@Serializable
data class GameStateJson(
    val player: PlayerJson,
    val location: String,
    val npcs_present: List<String>,
    val quest_log: List<String>,
    val inventory: List<String>,
    val summary: String
)

enum class ApiRole {
    DM, COMPANION
}

data class GameSettings(
    var isOfflineMode: Boolean = false,
    var isCompanionEnabled: Boolean = true,
    var apiRole: ApiRole = ApiRole.COMPANION, // Default: API is Companion
    var useGpu: Boolean = true, // Default: Use GPU if available
    var selectedModelName: String? = "gemma-3-1b-it-Q4_K_M.gguf", // Default to Gemma 3 1B
    var geminiApiKey: String = "" // User provided API Key
)
