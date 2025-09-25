package com.example.drawingapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.drawingapplication.Greeting
import com.example.drawingapplication.ui.theme.DrawingApplicationTheme
import kotlin.math.min

class DrawingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingApplicationTheme {
                DrawingCanvas()
            }
        }
    }
}

@Composable
fun DrawingCanvas() {
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf(listOf<Offset>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .height(350.dp)
                .width(350.dp)
                .background(Color.LightGray)
                .clipToBounds()
                //We capture touch input with
                // pointerInput and detectDragGestures.
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentStroke = listOf(offset)
                            //if you update current stroke live here not on DragEnd,
                            // then you do not need a second loop
                            strokes = strokes + listOf(currentStroke)
                        },
                        onDrag = { change, x ->
                            change.consume()
                            currentStroke = currentStroke + change.position
                            //if you update current stroke live here not on DragEnd,
                            // then you do not need a second loop
                            strokes = strokes.dropLast(1) + listOf(currentStroke)
                        },
                        onDragEnd = {
                            //strokes = strokes + listOf(currentStroke)
                            currentStroke = emptyList()
                        }
                    )
                }
        ) {
            // Draw all completed strokes
            strokes.forEach { stroke ->
                for (i in 0 until stroke.size - 1) {
                    drawLine(
                        color = Color.Black,
                        start = stroke[i],
                        end = stroke[i + 1],
                        strokeWidth = 8f
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            // TODO link buttons to ViewModel functionality
            // TODO replace text on buttons with icons
            Button(onClick = { placeholder() }, modifier = Modifier.padding(5.dp)) {
                Text("Colors")
            }
            Button(onClick = { placeholder() }, modifier = Modifier.padding(5.dp)) {
                Text("Shape")
            }
            Button(onClick = { placeholder() }, modifier = Modifier.padding(5.dp)) {
                Text("Size")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            // TODO link buttons to ViewModel functionality
            Button(onClick = { placeholder() }, modifier = Modifier.padding(5.dp)) {
                Text("Home")
            }
            Button(onClick = { placeholder() }, modifier = Modifier.padding(5.dp)) {
                Text("Save")
            }
        }
    }
}

fun placeholder() {}

// Below is currently unused code copied from the drawing demo
enum class BrushType {
    LINE, CIRCLE, RECTANGLE
}

@Composable
fun DrawingCanvasWithPoints(brushType: BrushType = BrushType.CIRCLE) {
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentStroke = listOf(offset)
                        strokes = strokes + listOf(currentStroke)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        currentStroke = currentStroke + change.position
                        strokes = strokes.dropLast(1) + listOf(currentStroke)
                    },
                    onDragEnd = { currentStroke = emptyList() }
                )
            }
    ) {
        strokes.forEach { stroke ->
            when (brushType) {
                BrushType.LINE -> {
                    for (i in 0 until stroke.size - 1) {
                        drawLine(Color.Black, stroke[i], stroke[i + 1], strokeWidth = 4f)
                    }
                }
                BrushType.CIRCLE -> {
                    stroke.forEach { point ->
                        drawCircle(Color.Red, radius = 15f, center = point)
                    }
                }
                BrushType.RECTANGLE -> {
                    stroke.forEach { point ->
                        drawRect(
                            Color.Black,
                            topLeft = Offset(point.x - 8f, point.y - 8f),
                            size = Size(16f, 16f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawCircle() {
    Column (Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Canvas(Modifier.size(100.dp)) {
            drawCircle(
                color = Color.Blue,
                radius = size.minDimension / 2
            )
        }
    }
}