package com.example.fixedassetinventory.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixedassetinventory.data.entity.Asset

@Composable
fun AssetCard(
    asset: Asset,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {

            // --- TOP RIGHT: Action Icons ---
            Row(
                modifier = Modifier.align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // --- MAIN CONTENT ---
            Column(modifier = Modifier.fillMaxWidth()) {
                // Mas malaki ang Description dahil ito ang "Human-friendly" name
//                Text(
//                    text = "${asset.description.ifBlank { "No Description" }} - ${asset.assetNumber}"  ,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp,
//                    color = Color.Black
//                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )) {
                            append(asset.description.ifBlank { "No Description" })
                        }
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        ) {
                            append(" - ")
                        }

                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        ) {
                            append(asset.assetNumber)
                        }
                    }
                )

                // Maliit na label para sa Asset Number (#A001 style)
//                Text(
//                    text = "#${asset.assetNumber}",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.primary,
//                    fontWeight = FontWeight.SemiBold
//                )

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.5.dp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = "Location: ${asset.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (asset.remarks.isNotBlank()) {
                            Text(
                                text = "Remarks: ${asset.remarks}",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(1.dp))
                        }
                    }
                    StatusBadge(status = asset.validate)
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val isFound = status.equals("Found", ignoreCase = true)
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isFound) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    ) {
        Text(
            text = if (isFound) "FOUND" else "NOT FOUND",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isFound) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    }
}