package com.example.drawingapplication

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class DrawingPoint(
    val offset : Offset,
    val color : Color,
    val size : Float,
    val shape : String
)

class DrawingViewModel : ViewModel() {

    //Drawing state
    private val penColor = MutableStateFlow(Color.Black)
    val penColorReadOnly : StateFlow<Color> = penColor

    fun changePenColor(color : Color){
        penColor.value = color
    }

    private val penSize = MutableStateFlow(8f)
    val penSizeReadOnly : StateFlow<Float> = penSize

    fun changePenSize(newSize : Float){
        penSize.value = newSize
    }

    private val penShape = MutableStateFlow("circle")
    val penShapeReadOnly : StateFlow<String> = penShape

    fun changePenShape(newShape : String){
        penShape.value = newShape
    }

    enum class PenOptions {
        NONE, COLOR, SHAPE, SIZE
    }

    private val penOptionsShown = MutableStateFlow(PenOptions.NONE)
    val penOptionsShownReadOnly : StateFlow<PenOptions> = penOptionsShown

    fun changeOrTogglePenOptions(newPenOptions : PenOptions) {
        if (penOptionsShown.value == newPenOptions) {
            penOptionsShown.value = PenOptions.NONE
        } else {
            penOptionsShown.value = newPenOptions
        }
    }

    // Drawing paths

    // each inner list represents one complete stroke (pen down -> drag -> pen up)
    private val strokes = MutableStateFlow<List<List<DrawingPoint>>>(emptyList())
    val strokesReadOnly : StateFlow<List<List<DrawingPoint>>> = strokes

    private val currentStroke = MutableStateFlow<List<DrawingPoint>>(emptyList())
    val currentStrokeReadOnly : StateFlow<List<DrawingPoint>> = currentStroke

    fun startStoke(offset : Offset){
        val drawingPoint = DrawingPoint(offset, penColor.value, penSize.value, penShape.value)
        currentStroke.value = listOf(drawingPoint)
        strokes.value = strokes.value + listOf(currentStroke.value)
    }

    fun addToStroke(offset : Offset){
        val drawingPoint = DrawingPoint(offset, penColor.value, penSize.value, penShape.value)
        currentStroke.value = currentStroke.value + drawingPoint
        strokes.value = strokes.value.dropLast(1) + listOf(currentStroke.value)
    }

    fun endStroke(){
        currentStroke.value = emptyList()
    }

}