package com.example.drawingapplication.data

import kotlinx.coroutines.flow.Flow

class DrawingRepository(private val drawingDao: DrawingDao) {

    val allDrawings: Flow<List<Drawing>> = drawingDao.getAllDrawings()

    suspend fun insertDrawing(drawing: Drawing) {
        drawingDao.insertDrawing(drawing)
    }
}