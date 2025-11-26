package com.example.drawingapplication.view

import android.R.attr.password
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.drawingapplication.FirebaseViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AuthScreen(navController : NavHostController, firebaseViewModel : FirebaseViewModel) {

    val currentUser by firebaseViewModel.currentUser.collectAsState()
    val errorMsg by firebaseViewModel.errorMsg.collectAsState()

    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("main")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = pw, onValueChange = { pw = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { firebaseViewModel.signIn(email, pw) }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { firebaseViewModel.signUp(email, pw) }) {
            Text("Sign Up")
        }

        errorMsg?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = Color.Red)
        }
    }
}