package com.example.drawingapplication.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.drawingapplication.DrawingViewModel
import com.example.drawingapplication.ui.theme.DrawingApplicationTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun DrawingCanvas(navController: NavHostController, drawingVM: DrawingViewModel) {
    val strokes by drawingVM.strokesReadOnly.collectAsState()

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
                //We capture touch input with pointerInput and detectDragGestures.
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            drawingVM.startStoke(offset)
                        },
                        onDrag = { change, x ->
                            change.consume()
                            drawingVM.addToStroke(change.position)
                        },
                        onDragEnd = {
                            drawingVM.endStroke()
                        }
                    )
                }
        ) {
            // Draw all completed strokes
            strokes.forEach { stroke ->
                stroke.forEach { point ->
                    if(point.shape == "circle") {
                        drawCircle(
                            color = point.color,
                            radius = point.size / 2,
                            center = point.offset
                        )
                    } else if (point.shape == "rectangle") {
                        drawRect(
                            color = point.color,
                            topLeft = Offset(point.offset.x - point.size/2, point.offset.y - point.size/2),
                            size = Size(point.size, point.size)
                        )
                    } else if (point.shape == "oval"){
                        drawOval(
                            color = point.color,
                            topLeft = Offset(point.offset.x - point.size/2, point.offset.y - point.size/3),
                            size = Size(point.size, point.size * 0.66f)
                        )
                    }
                }

                for (i in 0 until stroke.size - 1) {
                    drawLine(
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

                    val shapes = listOf("circle", "rectangle", "oval")

                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        Row(
                            Modifier.padding(8.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            shapes.forEach { shape ->
                                Button(
                                    onClick = { drawingVM.changePenShape(shape) },
                                    Modifier.padding(5.dp)
                                ) {
                                    Text(shape)
                                }
                            }

                        }
                    }
                } else if (options == DrawingViewModel.PenOptions.SIZE) {
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
            // TODO link buttons to ViewModel functionality; this is required in Phase 2
            Button(onClick = { navController.navigate("main") }, modifier = Modifier.padding(5.dp)) {
                Text("Home")
            }
            Button(onClick = { placeholder() }, modifier = Modifier.padding(5.dp)) {
                Text("Save")
            }
        }
    }
}

// Will be removed in Phase 2
fun placeholder(){}
fun openPenOptions(type: Int) {

}