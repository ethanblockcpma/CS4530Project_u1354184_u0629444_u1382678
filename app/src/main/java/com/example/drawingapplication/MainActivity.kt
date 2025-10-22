package com.example.drawingapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.drawingapplication.data.DrawingDatabase
import com.example.drawingapplication.data.DrawingRepository
import com.example.drawingapplication.ui.theme.DrawingApplicationTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.drawingapplication.navigation.AppNavHost

class MainActivity : ComponentActivity() {

    // This is not ideal. In a real application, you would use a dependency injection
    // framework like Hilt or Koin to manage the database and repository instances.
    private val database by lazy { DrawingDatabase.getDatabase(this) }
    private val repository by lazy { DrawingRepository(database.drawingDao()) }
    private val drawingViewModel: DrawingViewModel by viewModels {
        DrawingViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingApplicationTheme {
                val navController = rememberNavController()
                AppNavHost(navController, drawingViewModel)
            }
        }
    }
}

@Composable
fun SplashScreen(navController : NavHostController){
    var displayText by remember { mutableStateOf("Drawing App") }

    LaunchedEffect(Unit){
        delay(2000)
        navController.navigate("main")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(displayText, fontSize = 32.sp)
    }
}