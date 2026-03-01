package com.example.fixedassetinventory.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Eto yung nawawalang import!
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExportOptionButton(
    label: String,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer, // Default na kulay
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer   // Kulay ng text
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth() // Para pantay-pantay ang haba nila sa column
            .height(56.dp),  // Standard height para sa clickable buttons
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        // Dito natin ise-center ang label
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center // Eto ang "magic" para mag-center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}