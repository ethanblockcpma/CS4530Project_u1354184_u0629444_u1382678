package com.example.drawingapplication

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DrawingViewModel : ViewModel() {

    //Drawing state
    private val penColor = MutableStateFlow("black")
    val penColorReadOnly : StateFlow<String> = penColor

    fun changePenColor(color : String){
        penColor.value = color
    }

    private val penSize = MutableStateFlow(1)
    val penSizeReadonly : StateFlow<Int> = penSize

    fun changePenSize(newSize : Int){
        penSize.value = newSize
    }

    private val penShape = MutableStateFlow("circle")
    val penShapeReadOnly : StateFlow<String> = penShape

    fun changePenShape(newShape : String){
        penShape.value = newShape
    }

    // Drawing paths

    // each inner list represents one complete stroke (pen down -> drag -> pen up)
    private val strokes = MutableStateFlow<List<List<Offset>>>(emptyList())
    val strokesReadOnly : StateFlow<List<List<Offset>>> = strokes

    private val currentStroke = MutableStateFlow<List<Offset>>(emptyList())
    val currentStrokeReadOnly : StateFlow<List<Offset>> = currentStroke

    fun startStokes(offset : Offset){
        currentStroke.value = listOf(offset)
        strokes.value = strokes.value + listOf(currentStroke.value)
    }

    fun addToStroke(offset : Offset){
        currentStroke.value = currentStroke.value + offset
        strokes.value = strokes.value.dropLast(1) + listOf(currentStroke.value)
    }

    fun endStroke(){
        currentStroke.value = emptyList()
    }

}