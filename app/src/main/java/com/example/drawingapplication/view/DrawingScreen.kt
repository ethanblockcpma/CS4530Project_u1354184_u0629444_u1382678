package com.example.drawingapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.drawingapplication.DrawingViewModel
import com.example.drawingapplication.ui.theme.DrawingApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class DrawingScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrawingApplicationTheme {
                val drawingVM:DrawingViewModel = viewModel()
                DrawingCanvas(drawingVM)
            }
        }
    }
}

@Composable
fun DrawingCanvas(drawingVM: DrawingViewModel) {
    //var strokes by remember { mutableStateOf(listOf<List<Triple<Offset, Color, Float>>>()) }
    val strokes by drawingVM.strokesReadOnly.collectAsState()
    var currentStroke by remember { mutableStateOf(listOf<Triple<Offset, Color, Float>>()) }

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
                           // currentStroke = listOf(Triple(
                           //     offset,
                            //    drawingVM.penColorReadOnly.value,
                            //    drawingVM.penSizeReadOnly.value))
                            //if you update current stroke live here not on DragEnd,
                            // then you do not need a second loop
                            //strokes = strokes + listOf(currentStroke)
                            drawingVM.startStoke(offset)
                        },
                        onDrag = { change, x ->
                            change.consume()
                            //currentStroke = currentStroke + Triple(
                            //    change.position,
                            //    drawingVM.penColorReadOnly.value,
                            //    drawingVM.penSizeReadOnly.value)
                            //if you update current stroke live here not on DragEnd,
                            // then you do not need a second loop
                            //strokes = strokes.dropLast(1) + listOf(currentStroke)
                            drawingVM.addToStroke(change.position)
                        },
                        onDragEnd = {
                            //strokes = strokes + listOf(currentStroke)
                            //currentStroke = emptyList()
                            drawingVM.endStroke()
                        }
                    )
                }
        ) {
            // Draw all completed strokes
            strokes.forEach { stroke ->
                for (i in 0 until stroke.size - 1) {
                    drawLine(
                        //color = stroke[i].second,
                        //start = stroke[i].first,
                        //end = stroke[i + 1].first,
                        //strokeWidth = stroke[i].third
                        color = stroke[i].color,
                        start = stroke[i].offset,
                        end = stroke[i + 1].offset,
                        strokeWidth = stroke[i].size
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            // TODO replace text on buttons with icons
            Button(
                onClick = { drawingVM.changeOrTogglePenOptions(DrawingViewModel.PenOptions.COLOR) },
                modifier = Modifier.padding(5.dp)
            ) {
                Text("Colors")
            }
            Button(
                onClick = { drawingVM.changeOrTogglePenOptions(DrawingViewModel.PenOptions.SHAPE) },
                modifier = Modifier.padding(5.dp)
            ) {
                Text("Shape")
            }
            Button(
                onClick = { drawingVM.changeOrTogglePenOptions(DrawingViewModel.PenOptions.SIZE) },
                modifier = Modifier.padding(5.dp)
            ) {
                Text("Size")
            }
        }

        AnimatedVisibility(
            drawingVM.penOptionsShownReadOnly.collectAsState().value != DrawingViewModel.PenOptions.NONE,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            val options = drawingVM.penOptionsShownReadOnly.collectAsState().value
            val firstRowColors = listOf(Color.Black, Color.White, Color.Red, Color.Yellow, Color.Blue)
            val secondRowColors = listOf(Color.Green, Color.Magenta, Color.Cyan, Color.DarkGray, Color.Gray)
            Box(modifier = Modifier.width(300.dp).height(150.dp).background(Color.LightGray)) {
                if (options == DrawingViewModel.PenOptions.COLOR) {
                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        LazyRow(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            items(firstRowColors) {
                                Button(
                                    onClick = { drawingVM.changePenColor(it) },
                                    Modifier.size(45.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = it)
                                ) {}
                            }
                        }
                        LazyRow(Modifier.padding(8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            items(secondRowColors) {
                                Button(
                                    onClick = { drawingVM.changePenColor(it) },
                                    Modifier.size(45.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = it)
                                ) {}
                            }
                        }
                    }
                } else if (options == DrawingViewModel.PenOptions.SHAPE) {
                    // TODO implement shape options
                    Text("shape options")
                } else if (options == DrawingViewModel.PenOptions.SIZE) {
                    // TODO the biggest size looks pretty weird, should maybe improve
                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        Row(
                            Modifier.padding(8.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { drawingVM.changePenSize(8f) },
                                Modifier.size(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = drawingVM.penColorReadOnly.collectAsState().value)
                            ) {}
                            Button(
                                onClick = { drawingVM.changePenSize(50f) },
                                Modifier.size(80.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = drawingVM.penColorReadOnly.collectAsState().value)
                            ) {}
                            Button(
                                onClick = { drawingVM.changePenSize(100f) },
                                Modifier.size(120.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = drawingVM.penColorReadOnly.collectAsState().value)
                            ) {}
                        }
                    }
                }
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

fun placeholder(){}
fun openPenOptions(type: Int) {

}

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