package com.example.myapplication.game

import android.content.Context
import com.cactus.CactusLM
import com.cactus.CactusInitParams
import com.cactus.ChatMessage
import com.example.myapplication.network.GeminiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class AIService(private val context: Context) {
    private val cactusLM = CactusLM()
    // GeminiService is instantiated per-request with the user's API Key 
    
    private val _isModelReady = MutableStateFlow(false)
    val isModelReady: StateFlow<Boolean> = _isModelReady

    private val _initStatus = MutableStateFlow("Initializing...")
    val initStatus: StateFlow<String> = _initStatus

    private val _initError = MutableStateFlow<String?>(null)
    val initError: StateFlow<String?> = _initError

    var isOnlineMode = true

    suspend fun initialize(selectedModelName: String?) {
        withContext(Dispatchers.IO) {
            _initStatus.value = "Checking model file..."
            
            if (selectedModelName == null) {
                _initError.value = "No model selected."
                return@withContext
            }

            // 1. Check Internal Storage (filesDir/models/filename)
            val appDocDir = File(context.applicationContext.filesDir, "models")
            if (!appDocDir.exists()) appDocDir.mkdirs()
            val internalFile = File(appDocDir, selectedModelName)
            
            var modelPathToUse: String? = null
            
            if (internalFile.exists()) {
                _initStatus.value = "Found in Internal Storage"
                modelPathToUse = internalFile.absolutePath
            } else {
                // 2. Check Downloads Folder
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                val downloadFile = File(downloadsDir, selectedModelName)
                
                if (downloadFile.exists()) {
                    _initStatus.value = "Found in Downloads. Copying..."
                    try {
                        // Copy to internal storage to ensure native access
                        downloadFile.inputStream().use { input ->
                            internalFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        _initStatus.value = "Copy Complete"
                        modelPathToUse = internalFile.absolutePath
                    } catch (e: Exception) {
                        _initError.value = "Copy Failed: ${e.message}"
                        return@withContext
                    }
                }
            }
            
            if (modelPathToUse == null) {
                // If not found locally, try to initialize via SDK (which handles auto-download for slugs)
                _initStatus.value = "Not found locally. Attempting SDK auto-download..."
                android.util.Log.d("AIService", "Model $selectedModelName not found locally. Trying SDK init.")
                
                try {
                    cactusLM.initializeModel(CactusInitParams(model = selectedModelName, contextSize = 2048))
                    _isModelReady.value = true
                    _initStatus.value = "Ready (SDK)"
                    return@withContext
                } catch (e: Exception) {
                    _initError.value = "Model not found and SDK init failed: ${e.message}"
                    return@withContext
                }
            }
            
            try {
                val file = File(modelPathToUse)
                _initStatus.value = "Loading Neural Engine..."
                android.util.Log.d("AIService", "Initializing model: $modelPathToUse")
                android.util.Log.d("AIService", "File exists: ${file.exists()}")
                android.util.Log.d("AIService", "File size: ${file.length()}")
                android.util.Log.d("AIService", "Can read: ${file.canRead()}")
                
                cactusLM.initializeModel(CactusInitParams(model = modelPathToUse, contextSize = 2048))
                _isModelReady.value = true
                _initStatus.value = "Ready"
            } catch (e: Exception) {
                e.printStackTrace()
                val file = File(modelPathToUse)
                val sb = StringBuilder()
                sb.append("Init Failed: ${e.message}\n")
                sb.append("Path: $modelPathToUse\n")
                sb.append("Exists: ${file.exists()}\n")
                sb.append("Size: ${file.length()} bytes\n")
                sb.append("Can Read: ${file.canRead()}\n")
                try {
                    if (file.exists() && file.canRead()) {
                        val magic = ByteArray(4)
                        file.inputStream().use { it.read(magic) }
                        sb.append("Magic: ${String(magic)}")
                    }
                } catch (ioe: Exception) {
                    sb.append("Magic Read Failed: ${ioe.message}")
                }
                _initError.value = sb.toString()
            }
        }
    }

    suspend fun downloadModelFromUrl(url: String, filename: String, onProgress: (Float) -> Unit) {
        withContext(Dispatchers.IO) {
            try {
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                
                val modelFile = File(downloadsDir, filename)
                val tempFile = File(downloadsDir, "$filename.tmp")

                val connection = URL(url).openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = true
                connection.connect()
                
                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception("Server returned $responseCode for URL: $url")
                }
                
                val fileLength = connection.contentLength

                connection.inputStream.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        val data = ByteArray(4096)
                        var total: Long = 0
                        var count: Int = input.read(data)
                        while (count != -1) {
                            total += count
                            output.write(data, 0, count)
                            if (fileLength > 0) {
                                onProgress(total.toFloat() / fileLength)
                            }
                            count = input.read(data)
                        }
                    }
                }
                
                // Verify GGUF Magic Bytes (GGUF)
                val magic = ByteArray(4)
                java.io.FileInputStream(tempFile).use { it.read(magic) }
                val magicString = String(magic)
                if (magicString != "GGUF") {
                    throw Exception("Invalid file format. Header: $magicString. Expected: GGUF")
                }

                if (modelFile.exists()) modelFile.delete()
                tempFile.renameTo(modelFile)
                onProgress(1.0f)
                
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    // Streaming State
    private val _currentGeneration = MutableStateFlow("")
    val currentGeneration: StateFlow<String> = _currentGeneration.asStateFlow()
    
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    suspend fun getDMResponse(prompt: String, session: GameSession, settings: GameSettings, memoryManager: MemoryManager): String {
        _isGenerating.value = true
        val gameStateJson = memoryManager.getGameStateJson(session)
        
        // --- PASS 1: THE STORYTELLER ---
        val storytellerPrompt = """
### SYSTEM
You are an expert Dungeon Master running a dark fantasy RPG.
Your goal is to narrate the next scene based on the player's action.
Focus ONLY on the story and description. Do NOT output any JSON or game stats yet.

### CURRENT GAME STATE
$gameStateJson

### PLAYER ACTION
"$prompt"

### INSTRUCTIONS
Narrate the result of the player's action. Be descriptive and immersive.
""".trimIndent()
        
        var narrativeResponse = ""
        _currentGeneration.value = "" // Reset streaming buffer

        // If Online and API Role is DM, use Gemini
        if (!settings.isOfflineMode && settings.apiRole == ApiRole.DM) {
             val geminiService = GeminiService(settings.geminiApiKey)
             narrativeResponse = geminiService.generateContent(storytellerPrompt)
             _currentGeneration.value = narrativeResponse // Update stream for UI
        } else {
            // Otherwise use Local Cactus
            if (!_isModelReady.value) return "DM is warming up... (Model loading)"
            
            val result = cactusLM.generateCompletion(
                messages = listOf(ChatMessage(content = storytellerPrompt, role = "user")),
                onToken = { token, _ ->
                    _currentGeneration.value += token
                }
            )
            narrativeResponse = result?.response ?: "..."
        }
        
        val cleanedNarrative = cleanResponse(narrativeResponse)
        _currentGeneration.value = "" // Clear stream after done
        
        // --- PASS 2: THE ACCOUNTANT ---
        // We run this AFTER the narrative is generated to ensure the state matches the story.
        val accountantPrompt = """
### SYSTEM
You are the Game State Manager. Your job is to update the JSON game state based on the latest turn.

### CURRENT GAME STATE
$gameStateJson

### LATEST STORY
"$cleanedNarrative"

### INSTRUCTIONS
1. Analyze the STORY and update the Game State (HP, Money, Location, Inventory, Summary, etc.) to match what happened.
2. Output ONLY the updated JSON object. Do not add any markdown or explanation.
3. The JSON must match the structure of the Current Game State.
""".trimIndent()

        var jsonResponse = ""
        if (!settings.isOfflineMode && settings.apiRole == ApiRole.DM) {
             val geminiService = GeminiService(settings.geminiApiKey)
             jsonResponse = geminiService.generateContent(accountantPrompt)
        } else {
             val result = cactusLM.generateCompletion(
                messages = listOf(ChatMessage(content = accountantPrompt, role = "user"))
            )
            jsonResponse = result?.response ?: ""
        }

        // Parse JSON from response
        // Try to find JSON block, or just parse the whole thing if it's raw JSON
        val jsonRegex = "```json([\\s\\S]*?)```".toRegex()
        val matchResult = jsonRegex.find(jsonResponse)
        val jsonString = matchResult?.groupValues?.get(1)?.trim() ?: jsonResponse.trim()
        
        if (jsonString.isNotEmpty()) {
            memoryManager.updateGameStateFromJson(jsonString, session)
        }
        
        _isGenerating.value = false
        return cleanedNarrative
    }

    private fun cleanResponse(response: String): String {
        android.util.Log.d("AIService", "Raw Response before cleaning: $response")
        
        // Remove <thinking> and <think> tags
        // Handle complete tags
        var cleaned = response.replace("<thinking>[\\s\\S]*?</thinking>".toRegex(), "")
        cleaned = cleaned.replace("<think>[\\s\\S]*?</think>".toRegex(), "")
        
        // Handle unclosed tags (if model runs out of context)
        if (cleaned.contains("<thinking>")) {
            cleaned = cleaned.substringBefore("<thinking>")
        }
        if (cleaned.contains("<think>")) {
            cleaned = cleaned.substringBefore("<think>")
        }
        
        // Cleanup stray tags just in case
        cleaned = cleaned.replace("<thinking>", "").replace("</thinking>", "")
        cleaned = cleaned.replace("<think>", "").replace("</think>", "")
        
        return cleaned.trim()
    }

    suspend fun getCompanionResponse(prompt: String, contextInfo: String, settings: GameSettings): String {
        if (!settings.isCompanionEnabled) return ""

        val fullPrompt = "You are a helpful companion in a DnD game. Keep it brief.\nContext:\n$contextInfo\n\nPlayer: $prompt\nCompanion:"

        // If Online and API Role is Companion, use Gemini
        if (!settings.isOfflineMode && settings.apiRole == ApiRole.COMPANION) {
            val geminiService = GeminiService(settings.geminiApiKey)
            return geminiService.generateContent(fullPrompt)
        }

        // Otherwise use Local Cactus
        if (!_isModelReady.value) return "Companion is sleeping... (Model loading)"
        val result = cactusLM.generateCompletion(
            messages = listOf(ChatMessage(content = fullPrompt, role = "user"))
        )
        return cleanResponse(result?.response ?: "...")
    }

    suspend fun getAvailableModels(): List<com.cactus.CactusModel> {
        return cactusLM.getModels()
    }

    suspend fun downloadAndInitSDKModel(slug: String) {
        withContext(Dispatchers.IO) {
            _initStatus.value = "SDK Downloading: $slug..."
            try {
                cactusLM.downloadModel(slug)
                _initStatus.value = "SDK Initializing..."
                cactusLM.initializeModel(CactusInitParams(model = slug, contextSize = 2048))
                _isModelReady.value = true
                _initStatus.value = "Ready (SDK)"
            } catch (e: Exception) {
                e.printStackTrace()
                _initError.value = "SDK Error: ${e.message}"
            }
        }
    }
}
