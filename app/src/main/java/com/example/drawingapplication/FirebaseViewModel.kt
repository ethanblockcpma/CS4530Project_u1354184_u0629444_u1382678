package com.example.drawingapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingapplication.data.SharedDrawing
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

//data class for drawings stored in cloud
data class CloudDrawing(
    val id:String = "",
    val userId:String = "",
    val imageURL:String = "",
    val title:String = "",
    val timestamp: Long = 0
)

class FirebaseViewModel : ViewModel() {

    private val auth : FirebaseAuth = Firebase.auth
    private val storage = Firebase.storage
    private val firestore = Firebase.firestore

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser : StateFlow<FirebaseUser?> = _currentUser

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg : StateFlow<String?> = _errorMsg

    private val _cloudDrawings = MutableStateFlow<List<CloudDrawing>>(emptyList())
    val cloudDrawings:StateFlow<List<CloudDrawing>> = _cloudDrawings

    private val _sharedDrawings = MutableStateFlow<List<SharedDrawing>>(emptyList())
    val sharedDrawings: StateFlow<List<SharedDrawing>> = _sharedDrawings

    fun signIn(email: String, pw: String) {
        viewModelScope.launch{
            try{
                auth.signInWithEmailAndPassword(email, pw).await()
                _currentUser.value = auth.currentUser
                _errorMsg.value = null
                //once logged in load cloud drawings
                loadMyCloudDrawings()
                loadSharedDrawings()
            } catch (e: Exception) {
                _errorMsg.value = e.message
            }
        }
    }

    fun signUp(email: String, pw: String) {
        viewModelScope.launch {
            try{
                auth.createUserWithEmailAndPassword(email, pw).await()
                _currentUser.value = auth.currentUser
                _errorMsg.value = null
            } catch (e:Exception) {
                _errorMsg.value = e.message
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _errorMsg.value = null
        _cloudDrawings.value = emptyList()
    }

    fun clearError() {
        _errorMsg.value = null
    }

    fun uploadDrawingToCloud(file : File, title:String){
        viewModelScope.launch {
            try{

                val userId = auth.currentUser?.uid
                //if no user logged in stop here
                //return@launch means exit this early
                if(userId == null){
                    return@launch
                }

                //upload to firebase storage
                val storageRef = storage.reference.child("drawings/$userId/${file.name}")
                storageRef.putFile(android.net.Uri.fromFile(file)).await()

                //get download url
                val imageUrl = storageRef.downloadUrl.await().toString()

                //save metadata
                //mapOf = like a dictionary w/ key-value pairs. recommended by claude
                val drawingData = mapOf(
                    "userId" to userId, //who owns this drawing
                    "imageUrl" to imageUrl,//where to download it
                    "title" to title,  // name of drawing
                    "timestamp" to System.currentTimeMillis() //when uploaded
                )
                //add data as a new doc in user_drawings
                firestore.collection("user_drawings").add(drawingData).await()

                //reload drawings to show new one
                loadMyCloudDrawings()

            } catch (e:Exception) {
                _errorMsg.value = "Upload failed: ${e.message}"
            }
        }
    }

    fun shareDrawingWithEmail(file: File, title: String, receiverEmail: String) {
        viewModelScope.launch {
            try {
                val senderId = auth.currentUser?.uid
                if (senderId == null) {
                    _errorMsg.value = "User not logged in."
                    return@launch
                }

                val storagePath = "drawings/$senderId/${file.name}"
                val storageRef = storage.reference.child(storagePath)
                storageRef.putFile(android.net.Uri.fromFile(file)).await()

                val imageUrl = storageRef.downloadUrl.await().toString()

                val sharedDrawingData = SharedDrawing(
                    imageUrl = imageUrl,
                    senderId = senderId,
                    receiverEmail = receiverEmail
                )

                firestore.collection("shared_drawings").add(sharedDrawingData).await()
                _errorMsg.value = "Drawing shared successfully with $receiverEmail"

            } catch (e: Exception) {
                _errorMsg.value = "Failed to share drawing: ${e.message}"
            }
        }
    }

    fun loadMyCloudDrawings() {
        viewModelScope.launch {
            try{

                val userId = auth.currentUser?.uid

                if(userId == null){
                    return@launch
                }

                //query firestore for all drawings with matching userId
                val query = firestore.collection("user_drawings")
                    .whereEqualTo("userId", userId).get().await()

                val drawings = mutableListOf<CloudDrawing>()

                for(document in query.documents){
                    //get data from doc and create CloudDrawing
                    val drawing = CloudDrawing(
                        id = document.id,
                        userId = document.getString("userId") ?: "",
                        imageURL = document.getString("imageUrl") ?: "",
                        title = document.getString("title") ?: "",
                        timestamp = document.getLong("timestamp") ?: 0
                    )
                    drawings.add(drawing)
                }

                _cloudDrawings.value = drawings

            } catch(e:Exception) {
                _errorMsg.value = "Failed to load drawings: ${e.message}"
            }
        }
    }
    
    fun unshareDrawing(documentId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("shared_drawings").document(documentId).delete().await()
                loadSharedDrawings() // Refresh the list
            } catch (e: Exception) {
                _errorMsg.value = "Failed to unshare drawing: ${e.message}"
            }
        }
    }

    fun loadSharedDrawings() {
        viewModelScope.launch {
            try {
                val currentUserEmail = auth.currentUser?.email
                if (currentUserEmail == null) {
                    return@launch
                }

                val query = firestore.collection("shared_drawings")
                    .whereEqualTo("receiverEmail", currentUserEmail).get().await()

                val drawings = query.documents.mapNotNull { document ->
                    document.toObject(SharedDrawing::class.java)
                }
                _sharedDrawings.value = drawings
            } catch (e: Exception) {
                _errorMsg.value = "Failed to load shared drawings: ${e.message}"
            }
        }
    }

}
