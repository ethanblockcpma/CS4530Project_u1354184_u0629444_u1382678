package com.example.drawingapplication.data

import kotlinx.serialization.Serializable


// - data models for google cloud vision api requests and responses
// - these models structure the json data sent to and received
//    from the vision api for object detection + analysis


//Request models

//top level wrapper sent to vision api, containing
// list of image analysis requests
@Serializable
data class VisionRequest(
    val requests: List<ImageRequest>
)

//individual image analysis request, specifies image and what features
// to detect
@Serializable
data class ImageRequest(
    val image : ImageContent,
    val features : List<Feature>
)

//container to hold image data, the base64 encoded image string sent to api
@Serializable
data class ImageContent(
    val content : String //base64 encoded image
)

//specifies what type of analysis to perform
//OBJECT_LOCALIZATION = object detection w/ bounding boxes
@Serializable
data class Feature(
    val type : String,
    val maxResults: Int = 10
)

//response models

//top level response from vision api, w/ list of analysis result
// for each image sent
@Serializable
data class VisionResponse(
    val responses : List<AnnotateImageResponse>
)

//analysis for a single image, contains all detected objects
// w/ their locations and confidence scores
@Serializable
data class AnnotateImageResponse(
    val localizedObjectAnnotations : List<DetectedObject>? = null
)

//a single detected object from the image
// includes name, confidence score, and bounding box cords
@Serializable
data class DetectedObject(
    val name:String,  //obj name
    val score:Float,   // confidence score
    val boundingPoly: BoundingBox
)

// the bounding box around detected objs
// contains 4 vertices that form a rectangle around that obj
@Serializable
data class BoundingBox(
    val normalizedVertices: List<Vertex>
)

// a single point of a bounding box
@Serializable
data class Vertex(
    val x : Float,
    val y : Float
)

