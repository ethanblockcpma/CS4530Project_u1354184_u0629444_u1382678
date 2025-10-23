package com.example.drawingapplication.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.drawingapplication.DrawingViewModel

@Composable
fun MainScreen(navController: NavHostController, drawingVM: DrawingViewModel) {
    val drawingsList by drawingVM.allDrawings.collectAsState(emptyList())
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("My Drawings", fontSize = 32.sp, modifier = Modifier.padding(bottom = 32.dp))
        LazyColumn(modifier = Modifier.padding(bottom = 32.dp)) {
            items(drawingsList) {
                Text("${it.title}")
            }
        }
        Button(onClick = { navController.navigate("drawing") }) {
            Text("New Drawing")
        }
    }
}