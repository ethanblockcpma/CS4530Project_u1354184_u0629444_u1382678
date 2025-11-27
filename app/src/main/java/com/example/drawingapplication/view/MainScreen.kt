package com.example.drawingapplication.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.drawingapplication.DrawingViewModel
import com.example.drawingapplication.FirebaseViewModel
import java.io.File

@Composable
fun MainScreen(navController: NavHostController, drawingVM: DrawingViewModel, firebaseVM : FirebaseViewModel) {
    val drawings by drawingVM.allDrawings.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Drawings", fontSize = 32.sp, modifier = Modifier.padding(bottom = 16.dp))

        Button(onClick = { navController.navigate("drawing") }) {
            Text("New Drawing")
        }

        Button(onClick = {
            firebaseVM.signOut()
            navController.navigate("auth") {
                //following line makes it so we cant go back to
                //other screens w/o auth
                popUpTo("main") {inclusive = true}
            }
        }) {
            Text("Sign out")
        }

        // Grid of saved drawings (3 columns like Instagram)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize().padding(top = 16.dp)
        ) {
            items(drawings) { drawing ->
                Image(
                    painter = rememberAsyncImagePainter(File(drawing.filePath)),
                    contentDescription = drawing.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}