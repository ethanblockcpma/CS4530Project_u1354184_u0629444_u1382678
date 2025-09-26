package com.example.drawingapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.drawingapplication.ui.theme.DrawingApplicationTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import com.example.drawingapplication.view.DrawingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingApplicationTheme {
                SplashScreen(
                    onSplashComplete = {
                        loadDrawingScreen()
                    }
                )
            }
        }
    }

    // TODO For now we go straight to the drawing screen because the main screen isn't implemented yet
    fun loadDrawingScreen() {
        val intent = Intent(this, DrawingScreen::class.java)
        startActivity(intent)
    }
}

@Composable
fun SplashScreen(onSplashComplete: () -> Unit ){
    var displayText by remember { mutableStateOf("Drawing App") }

    LaunchedEffect(Unit){
        delay(2000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(displayText, fontSize = 32.sp)
    }
}