package com.example.drawingapplication.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ShareDialog(
    onDismiss: () -> Unit,
    onShare: (String) -> Unit
) {
    val email = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Share Drawing")
        },
        text = {
            Column {
                Text(text = "Enter the email address of the user you want to share with.")
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text(text = "Email") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onShare(email.value) }) {
                Text(text = "Share")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

@Preview
@Composable
fun ShareDialogPreview() {
    ShareDialog(onDismiss = {}, onShare = {})
}
