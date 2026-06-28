package com.rpd.data.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rpd.data.ui.connect.ConnectScreen
import com.rpd.data.ui.recording.RecordingScreen

object Routes {
    const val CONNECT = "connect"
    const val RECORDING = "recording"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.CONNECT
    ) {
        composable(Routes.CONNECT) {
            ConnectScreen(navController)
        }

        composable(Routes.RECORDING) {
            RecordingScreen(navController)
        }
    }
}