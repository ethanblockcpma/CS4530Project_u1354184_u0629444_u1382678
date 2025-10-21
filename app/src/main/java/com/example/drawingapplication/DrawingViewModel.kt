package com.example.drawingapplication

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.drawingapplication.data.Drawing
import com.example.drawingapplication.data.DrawingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DrawingPoint(
    val offset : Offset,
    val color : Color,
    val size : Float,
    val shape : String
)

class DrawingViewModel(private val repository: DrawingRepository) : ViewModel() {

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

    private val _allDrawings = MutableStateFlow<List<Drawing>>(emptyList())
    val allDrawings: StateFlow<List<Drawing>> = _allDrawings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allDrawings.collect {
                _allDrawings.value = it
            }
        }
    }

    fun insertDrawing(drawing: Drawing) = viewModelScope.launch {
        repository.insertDrawing(drawing)
    }
}

class DrawingViewModelFactory(private val repository: DrawingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DrawingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DrawingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}