package com.example.fixedassetinventory.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fixedassetinventory.data.entity.Asset
import com.example.fixedassetinventory.ui.components.AssetCard
import com.example.fixedassetinventory.ui.components.BaseDialog
import com.example.fixedassetinventory.ui.components.ExportOptionButton
import com.example.fixedassetinventory.viewmodel.AssetViewModel
import com.example.fixedassetinventory.viewmodel.ValidationStatus


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AssetViewModel,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // ----------------------------------------------------------------------------------
    //                                  ASSET MANAGEMENT DECLARATION
    //------------------------------------------------------------------------------------
    var showAddDialog by remember { mutableStateOf(false) }
    var assetByEdit by remember { mutableStateOf<Asset?>(null) }
    var assetByDelete by remember { mutableStateOf<Asset?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }


    // ----------------------------------------------------------------------------------
    //                                  DATABASE DECLARATION
    //------------------------------------------------------------------------------------
    val context = LocalContext.current
    val assetList by viewModel.assets.collectAsState(initial = emptyList())



    // ----------------------------------------------------------------------------------
    //                                  FILE HANDLING SECTION
    //------------------------------------------------------------------------------------
    val summary = viewModel.importSummary
    val filePickerLauncher = rememberLauncherForActivityResult(contract =  ActivityResultContracts.GetContent()) {
        uri: Uri? ->
        uri?.let {
            println("File Selected: $it")
            viewModel.importCsv(context, it)
        }
    }


    // ----------------------------------------------------------------------------------
    //                                       DIALOG SECTION
    //-----------------------------------------------------------------------------------

    // CREATE DIALOG
    if (showAddDialog) {
        var newAssetNo by remember { mutableStateOf("") }
        var newDesc by remember { mutableStateOf("") }
        var newLoc by remember { mutableStateOf("") }
        var newRem by remember { mutableStateOf("") }

//        androidx.compose.material3.AlertDialog(
//            onDismissRequest = {
//                showAddDialog = false
//                viewModel.clearAssetError()
//            },
//            title = { Text("Asset Registration", fontWeight = FontWeight.Bold)},
//            text = {
//                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(24.dp), // Fixed height para reserved ang space
//                        contentAlignment = Alignment.CenterStart
//                    ) {
//                        if (viewModel.assetError != null) {
//                            Text(
//                                text = viewModel.assetError!!,
//                                color = Color.Red,
//                                style = MaterialTheme.typography.bodySmall,
//                                fontWeight = FontWeight.Medium
//                            )
//                        }
//                    }
//                    OutlinedTextField(
//                        value = newAssetNo,
//                        onValueChange = { newAssetNo = it },
//                        label = { Text("Asset Number")},
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    OutlinedTextField(
//                        value = newDesc,
//                        onValueChange = { newDesc = it },
//                        label = { Text("Description")},
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    OutlinedTextField(
//                        value = newLoc,
//                        onValueChange = { newLoc = it },
//                        label = { Text("Location")},
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                    OutlinedTextField(
//                        value = newRem,
//                        onValueChange = { newRem = it },
//                        label = { Text("Remarks")},
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//            },
//            confirmButton = {
//                androidx.compose.material3.Button(
//                    onClick = {
//                        viewModel.addAsset(newAssetNo, newDesc, newLoc, newRem) {
//                                showAddDialog = false
//                                newAssetNo = ""; newDesc = ""; newLoc = ""; newRem = ""
//                            }
//                        }
//                    ) {
//                        Text("Save Asset")
//                    }
//                },
//                dismissButton = {
//                    androidx.compose.material3.TextButton(onClick = { showAddDialog = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
        BaseDialog(
            onDismissRequest = {
                showAddDialog = false
                viewModel.closeDialog()
            },
            title = "Asset Registration",
            confirmButton = {
                Button(onClick = {
                    viewModel.addAsset(newAssetNo, newDesc, newLoc, newRem) {
                        showAddDialog = false
                        newAssetNo = ""; newDesc = ""; newLoc = ""; newRem = ""
                    }
                }) {
                    Text("Save Asset")
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (viewModel.assetError != null) {
                    Text(
                        text = viewModel.assetError!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

//                val assetFields = listOf(
//                    AssetField("Asset Number")
//                )
            }
        }
    }

    // EDIT DIALOG
    if (assetByEdit != null) {
        var desc by remember { mutableStateOf(assetByEdit!!.description) }
        var loc by remember { mutableStateOf(assetByEdit!!.location) }
        var rem by remember { mutableStateOf(assetByEdit!!.remarks) }

        androidx.compose.material3.AlertDialog(
            onDismissRequest = {  assetByEdit = null },
            title = { Text("Edit Asset: ${assetByEdit!!.assetNumber}", fontWeight = FontWeight.Bold)},
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description")})
                    OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("Location")} )
                    OutlinedTextField(value = rem, onValueChange = { rem = it }, label = { Text("Remarks")} )
                }
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        val updateAsset = assetByEdit!!.copy(
                            description = desc,
                            location = loc,
                            remarks = rem,
                        )
                        viewModel.updateAsset(updateAsset)
                        assetByEdit = null
                    },
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { assetByEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DELETE DIALOG
    if (assetByDelete != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { assetByDelete = null },
            title = { Text("Confirm Delete", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete asset ${assetByDelete!!.assetNumber}?")},
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        viewModel.deleteAsset(assetByDelete!!)
                        assetByDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { assetByDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // IMPORT DIALOG
    if (summary != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.clearImportSummary() },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { viewModel.clearImportSummary() }) {
                    Text("Ok")
                }
            },
            title = { Text("Import Result", fontWeight = FontWeight.Bold)},
            text = {
                Column {
                    Text("Total Records: ${summary.total}")
                    Text("Successfully imported: ${summary.success}")
                    Text("Skipped Duplicate Asset: ${summary.skipped}", color = Color.Red)
                }
            }
        )
    }

    // EXPORT DIALOG
    if (showExportDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Export Report", fontWeight = FontWeight.Bold)},
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select Export File Format")

                    ExportOptionButton(
                        label = "CSV",
                        onClick = {
                            viewModel.exportToCsv(context)
                            showExportDialog = false
                        }
                    )

                    ExportOptionButton(
                        label = "Excel",
                        onClick = {
                            viewModel.exportToExcel(context)
                            showExportDialog = false
                        }
                    )

                    ExportOptionButton(
                        label = "PDF",
                        onClick = {
                            viewModel.exportToPdf(context)
                            showExportDialog = false
                        }
                    )


                }
            },
            confirmButton = {},
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showExportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ----------------------------------------------------------------------------------
    //                                  MAIN VIEW SECTION
    //------------------------------------------------------------------------------------
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Fixed Asset Inventory",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Add Asset") },
                            onClick = {
                                viewModel.clearAssetError()
                                showMenu = false
                                showAddDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Import") },
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, contentDescription = null)},
                            onClick = {
                                showMenu = false
                                filePickerLauncher.launch("*/*")
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Export") },
                            onClick = {
                                showMenu = false
//                                viewModel.exportToCsv(context)
                                showExportDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.ArrowDropDown,
                                contentDescription = null)
                            }
                        )
                        Divider()
                        DropdownMenuItem(text = { Text("Logout") },
                            leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        },

    ) {padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .padding(16.dp)) {

            val assetExists = assetList.any { it.assetNumber.equals(searchText.trim(), ignoreCase = true)}

            val currentStatusLabel = if (assetExists) "FOUND" else "NOT FOUND"
            val statusColor = if (assetExists) Color(0xFF2E7D32) else Color.Red
            val statusIcon = if (assetExists) Icons.Default.CheckCircle else Icons.Default.Close


            if (viewModel.showValidationDialog && viewModel.validationStatus == ValidationStatus.NOT_EXIST) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { viewModel.closeDialog() },
                    confirmButton = {
                        androidx.compose.material3.TextButton(onClick = { viewModel.closeDialog() }) {
                            Text("Retry")
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    },
                    title = { Text("Item Not Found", fontWeight = FontWeight.Bold) },
                    text = { Text("Item does not exist.") }
                )
            }
            // Search

            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            if (it.isEmpty()) {
                                viewModel.resetValidationStatus()
                            }
                        },
                        placeholder = { Text("Search Asset Number...", color = Color.Gray)},
                        leadingIcon = {
                            IconButton(onClick = { viewModel.validateAsset(searchText)}) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search Button",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        shape = RoundedCornerShape(25.dp),
                        singleLine = true,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue.copy(alpha = 0.2f),
                            unfocusedBorderColor = Color.Blue.copy(alpha = 0.1f),
                            focusedLabelColor = Color.Blue,
                            cursorColor = Color.Blue,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        )
                    )
                }

                if (searchText.isNotEmpty()) {
                    androidx.compose.material3.ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp)
                            .padding(horizontal = 8.dp),
                        elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = statusIcon,
                                contentDescription = null,
                                tint = statusColor
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentStatusLabel,
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }



            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                val filteredList = assetList.filter {
                    it.assetNumber.contains(searchText, ignoreCase = true) || it.description.contains(searchText, ignoreCase = true)
                }

                if (filteredList.isEmpty() && searchText.isNotEmpty()) {
                    item {
                        Text(
                            "Item does not exist.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    }
                }
                items(filteredList) { asset ->
                    AssetCard(
                        asset = asset,
                        onEdit = {
                            assetByEdit = asset
                        },
                        onDelete = {
                            assetByDelete = asset
                        }
                    )

                }
            }
        }

    }
}


