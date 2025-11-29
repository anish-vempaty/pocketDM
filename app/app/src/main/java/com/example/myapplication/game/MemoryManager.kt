package com.example.myapplication.game

class MemoryManager {
    private val lorebook = mutableMapOf<String, String>()
    private val importantEvents = mutableListOf<String>()

    fun addLore(key: String, value: String) {
        lorebook[key] = value
    }

    fun getLore(key: String): String? {
        return lorebook[key]
    }

    fun recordEvent(event: String) {
        importantEvents.add(event)
    }

    fun getSummary(): String {
        return "Key Events: ${importantEvents.joinToString("; ")}"
    }
    
    fun getContextForAI(): String {
        val loreSummary = lorebook.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        val eventSummary = importantEvents.takeLast(5).joinToString("\n")
        return "Lore:\n$loreSummary\n\nRecent Events:\n$eventSummary"
    }
    
    fun getGameStateJson(session: GameSession): String {
        val state = GameStateJson(
            player = PlayerJson(
                `class` = session.player.characterClass.className,
                hp = session.player.hp,
                money = session.player.money
            ),
            location = session.location,
            npcs_present = session.npcs,
            quest_log = listOf(session.currentQuest),
            inventory = session.player.inventory,
            summary = session.summary
        )
        return kotlinx.serialization.json.Json.encodeToString(GameStateJson.serializer(), state)
    }

    fun updateGameStateFromJson(jsonString: String, session: GameSession) {
        try {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            val state = json.decodeFromString(GameStateJson.serializer(), jsonString)
            
            // Update Session
            session.player.hp = state.player.hp
            session.player.money = state.player.money
            session.location = state.location
            session.npcs.clear()
            session.npcs.addAll(state.npcs_present)
            session.player.inventory.clear()
            session.player.inventory.addAll(state.inventory)
            session.summary = state.summary
            
            if (state.quest_log.isNotEmpty()) {
                session.currentQuest = state.quest_log.first()
            }
        } catch (e: Exception) {
            android.util.Log.e("MemoryManager", "Failed to parse JSON state: ${e.message}")
        }
    }
}
