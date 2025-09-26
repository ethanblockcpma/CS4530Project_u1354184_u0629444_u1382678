package com.example.drawingapplication.model
import android.graphics.Bitmap
data class Drawing(
    val id: String,
    val paths: List<DrawingPath>,
    val width: Int,
    val height: Int,
    val thumbnail: Bitmap? = null, //Bitmap thumbnail of drawing
    val lastModified: Long = System.currentTimeMillis() //when drawing was last modified/saved

)
