package com.example.fixedassetinventory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BaseDialog(
    onDismissRequest: () -> Unit,
    title: String,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButtonText: String = "Cancel",
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                content()
            }
        },
        confirmButton = {
            confirmButton?.invoke()
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(dismissButtonText)
            }
        }
    )
}