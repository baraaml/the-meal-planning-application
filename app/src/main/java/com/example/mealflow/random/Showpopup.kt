package com.example.mealflow.random

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.example.mealflow.viewModel.RegisterViewModel


@Composable
fun ErrorPopupRegister(viewModel: RegisterViewModel) {
    val showErrorPopup by viewModel.showErrorPopup.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    if (showErrorPopup) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissErrorPopup() },
            title = { Text(text = "Error") },
            text = { Text(text = errorMessage ?: "Unknown error occurred") },
            confirmButton = {
                Button(onClick = { viewModel.dismissErrorPopup() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun InputPopupDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    initialText: String = ""
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registration error") },
        text = { Text(text = initialText) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "OK")
            }
        }
    )
}
