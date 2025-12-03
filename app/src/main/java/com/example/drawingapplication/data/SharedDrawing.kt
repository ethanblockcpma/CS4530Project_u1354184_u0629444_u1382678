package com.example.drawingapplication.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class SharedDrawing(
    @DocumentId val documentId: String = "",
    val imageUrl: String = "",
    val senderId: String = "",
    val receiverEmail: String = "",
    @ServerTimestamp val timestamp: Date? = null
)
