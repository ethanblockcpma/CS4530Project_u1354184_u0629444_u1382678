package com.example.drawingapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.drawingapplication.DrawingViewModel
import com.example.drawingapplication.FirebaseViewModel
import com.example.drawingapplication.SplashScreen
import com.example.drawingapplication.view.AuthScreen
import com.example.drawingapplication.view.DrawingCanvas
import com.example.drawingapplication.view.MainScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    drawingViewModel: DrawingViewModel,
    firebaseViewModel : FirebaseViewModel,
    startDestination: String = "splash")
{
    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("auth") {
            AuthScreen(navController, firebaseViewModel)
        }

        composable("main") {
            MainScreen(navController, drawingViewModel, firebaseViewModel)
        }

        composable("drawing") {
            DrawingCanvas(navController, drawingViewModel)
        }
    }
}