package com.example.fixedassetinventory.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fixedassetinventory.ui.state.AssetFormState


@Composable
fun AssetFormContent (
    formState: AssetFormState,
    error: String? = null,
    isEditMode: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        error?.let {
            Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
    }
    formState.getFields(isEditMode).forEach {field ->
        OutlinedTextField(
            value = field.value,
            onValueChange = field.onValueChange,
            label = { Text(field.label) },
            enabled = field.enabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}