package com.example.myapplication.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GeminiService(private val apiKey: String) {
    
    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonBody = JSONObject()
            val contents = JSONObject()
            val parts = JSONObject()
            parts.put("text", prompt)
            contents.put("parts", listOf(parts))
            contents.put("role", "user")
            jsonBody.put("contents", listOf(contents))

            connection.outputStream.use { it.write(jsonBody.toString().toByteArray()) }

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(response)
                // Parse response to get text
                // Structure: candidates[0].content.parts[0].text
                val candidates = jsonResponse.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).getJSONObject("content")
                    val partsArray = content.getJSONArray("parts")
                    if (partsArray.length() > 0) {
                        return@withContext partsArray.getJSONObject(0).getString("text")
                    }
                }
                return@withContext "Gemini is silent."
            } else {
                return@withContext "Error: ${connection.responseMessage}"
            }
        } catch (e: Exception) {
            return@withContext "Exception: ${e.message}"
        }
    }
}
