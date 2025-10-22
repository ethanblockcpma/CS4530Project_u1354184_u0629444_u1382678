package com.example.drawingapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.drawingapplication.DrawingViewModel
import com.example.drawingapplication.SplashScreen
import com.example.drawingapplication.view.DrawingCanvas
import com.example.drawingapplication.view.MainScreen

@Composable
fun AppNavHost(navController: NavHostController, drawingViewModel: DrawingViewModel, startDestination: String = "splash") {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("main") {
            MainScreen(navController)
        }

        composable("drawing") {
            DrawingCanvas(navController, drawingViewModel)
        }
    }
}