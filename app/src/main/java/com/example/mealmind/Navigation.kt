package com.example.mealmind.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mealmind.ui.screens.camera.CameraScreen
import com.example.mealmind.ui.screens.result.ResultScreen
import java.net.URLDecoder

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "camera"
    ) {
        composable("camera") { CameraScreen(navController) }
        composable("result/{labels}") { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("labels") ?: ""
            ResultScreen(encodedLabels = encoded)
        }
    }
}