package com.example.drawingapplication.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class PenShape {
    CIRCLE, SQUARE //just some initial pen shapes, can add more later
}

data class PenSettings(
    val color: Color = Color.Black,
    val size: Dp = 5.dp,
    val shape: PenShape = PenShape.CIRCLE
)
