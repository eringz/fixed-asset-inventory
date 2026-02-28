package com.example.fixedassetinventory.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun ExportOptionButton(
    label: String,
//    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
//            androidx.compose.material3.Icon(
////                imageVector = icon,
//                contentDescription = null,
//                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
//            )
            androidx.compose.foundation.layout.Spacer(androidx.compose.ui.Modifier.width(12.dp))
            androidx.compose.material3.Text(
                text = label,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
        }
    }
}