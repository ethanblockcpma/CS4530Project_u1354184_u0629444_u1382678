package com.example.drawingapplication

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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