package com.example.drawingapplication.model
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
data class DrawingPath(
    val points: List<Offset>, //list of points (x,y coords) that make up the dawing
    val color: Color,
    val penWidth: Dp,
    val penShape: PenShape // we may want to store the shape used for a specific path if we
                           // allow changing shapes mid drawing for different pen strokes.
)
