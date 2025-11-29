package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cactus.CactusContextInitializer
import com.example.myapplication.game.AIService
import com.example.myapplication.game.GameSettings
import com.example.myapplication.game.MemoryManager
import com.example.myapplication.game.Player
import com.example.myapplication.ui.screens.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request All Files Access (Android 11+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (!android.os.Environment.isExternalStorageManager()) {
                val intent = android.content.Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = android.net.Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivity(intent)
            }
        } else {
            // Legacy Permissions
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1)
            }
        }

        CactusContextInitializer.initialize(applicationContext)
        
        val aiService = AIService(applicationContext)
        val memoryManager = MemoryManager()
        val settings = GameSettings()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                var player by remember { mutableStateOf<Player?>(null) }
                val scope = rememberCoroutineScope()



                Scaffold {
                    NavHost(navController, startDestination = "splash") {
                        composable("splash") {
                            SplashScreen {
                                navController.navigate("menu") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                        composable("menu") {
                            MainMenuScreen(
                                onStartGame = { navController.navigate("create_character") },
                                onOptions = { navController.navigate("options") },
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                        composable("options") {
                            OptionsScreen(
                                settings = settings,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("create_character") {
                            CharacterCreationScreen { newPlayer ->
                                player = newPlayer
                                navController.navigate("loading")
                            }
                        }
                        composable("loading") {
                            LoadingScreen(
                                aiService = aiService,
                                settings = settings,
                                onModelReady = {
                                    navController.navigate("game") {
                                        popUpTo("loading") { inclusive = true }
                                    }
                                },
                                onNavigateToModelSelection = {
                                    navController.navigate("model_selection") {
                                        popUpTo("loading") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("model_selection") {
                            ModelSelectionScreen(
                                aiService = aiService,
                                settings = settings,
                                onModelSelected = {
                                    navController.navigate("loading") {
                                        popUpTo("model_selection") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("discovery") {
                            DiscoveryScreen(
                                aiService = aiService,
                                settings = settings,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("game") {
                            if (player != null) {
                                GameScreen(player!!, aiService, memoryManager, settings)
                            }
                        }
                    }
                }
            }
        }
    }
}
