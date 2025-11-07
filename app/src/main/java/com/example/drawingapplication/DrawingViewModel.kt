package com.example.drawingapplication

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.drawingapplication.data.Drawing
import com.example.drawingapplication.data.DrawingRepository
//import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
//import com.example.drawingapplication.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class DrawingPoint(
    val offset : Offset,
    val color : Color,
    val size : Float,
    val shape : String
)

class DrawingViewModel(private val repository: DrawingRepository) : ViewModel() {

    //PHASE 3: Creating AI Model
    private val model = GenerativeModel(modelName = "gemini-2.5-flash",apiKey = BuildConfig.GEMINI_API_KEY)


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

    fun getDrawingAsPng(context: Context): File {
        // was getting weird ratio issues, convert dp -> pixels here
        val density = context.resources.displayMetrics.density
        val widthpx = (350*density).toInt()
        val heightpx = (350*density).toInt()

        //create bitmap with correct size, and a canvas to draw on that new bitmap
        val bitmap = Bitmap.createBitmap(widthpx,heightpx,Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        //background
        canvas.drawColor(android.graphics.Color.LTGRAY)
        val paint = android.graphics.Paint()

        //loop thru saved strokes, reconstruct the drawing on this bitmap that will be saved
        strokes.value.forEach { stroke ->
            for (i in 0 until stroke.size - 1) {
                paint.color = android.graphics.Color.argb(
                    (stroke[i].color.alpha * 255).toInt(),
                    (stroke[i].color.red * 255).toInt(),
                    (stroke[i].color.green * 255).toInt(),
                    (stroke[i].color.blue * 255).toInt()
                )
                paint.strokeWidth = stroke[i].size
                canvas.drawLine(
                    stroke[i].offset.x,
                    stroke[i].offset.y,
                    stroke[i + 1].offset.x,
                    stroke[i + 1].offset.y,
                    paint
                )
            }

        }

        //save to bitmap as png file
        val count = allDrawings.value.size + 1
        val filename = "Drawing${count}.png"
        val title = "Drawing${count}"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun saveDrawing(context : Context){
        viewModelScope.launch{
            val file = getDrawingAsPng(context)
            //save to db (metadata stuff)
            repository.insertDrawing(com.example.drawingapplication.data.Drawing(
                title = file.name,
                filePath = file.absolutePath
            ))
        }
    }

    fun shareDrawing(context: Context) {
        viewModelScope.launch {
            val file = getDrawingAsPng(context)
            repository.insertDrawing(com.example.drawingapplication.data.Drawing(
                title = file.name,
                filePath = file.absolutePath
            ))
            val uri = FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".fileprovider",
                file
            );
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/png"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
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