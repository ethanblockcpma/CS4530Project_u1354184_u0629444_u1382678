package com.example.drawingapplication.data

import com.example.drawingapplication.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json


class DrawingRepository(private val drawingDao: DrawingDao) {

    val allDrawings: Flow<List<Drawing>> = drawingDao.getAllDrawings()

    suspend fun insertDrawing(drawing: Drawing) {
        drawingDao.insertDrawing(drawing)
    }

    //phase 3

    //http client for api requests
    // uses ktor w/ json serialization to communicate with api
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                //isLenient = true
            })
        }
    }

    //sends an image to the api for analysis
    //param = imageBase64, image encoded as base64 String
    // returns a VisionResponse containing detected images w/
    //    bounding boxes + confidence score
    suspend fun analyzePicture(imageBase64 : String) : VisionResponse {
        val req = VisionRequest(
            requests = listOf(
                com.example.drawingapplication.data.ImageRequest(
                    image = ImageContent(content = imageBase64),
                    features = listOf(Feature(type = "OBJECT_LOCALIZATION"))
                )
            )
        )

        //post req to vision api endpoint w/ key
        return client.post("https://vision.googleapis.com/v1/images:annotte?hey=${BuildConfig.VISION_API_KEY}") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }.body()
    }
}