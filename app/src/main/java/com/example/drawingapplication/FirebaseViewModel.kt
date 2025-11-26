package com.example.drawingapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseViewModel : ViewModel() {

    private val auth : FirebaseAuth = Firebase.auth

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser : StateFlow<FirebaseUser?> = _currentUser

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg : StateFlow<String?> = _errorMsg

    fun signIn(email: String, pw: String) {
        viewModelScope.launch{
            try{
                auth.signInWithEmailAndPassword(email, pw).await()
                _currentUser.value = auth.currentUser
                _errorMsg.value = null
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
    }

    fun clearError() {
        _errorMsg.value = null
    }
}