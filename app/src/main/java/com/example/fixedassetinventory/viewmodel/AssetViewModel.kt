package com.example.fixedassetinventory.viewmodel

// mutalbleStateof Imports

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fixedassetinventory.data.dao.AssetDao
import com.example.fixedassetinventory.data.entity.Asset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

data class ImportSummary (
    val total: Int = 0,
    val success: Int = 0,
    val skipped: Int = 0
)

enum class ValidationStatus {
    INITIAL,
    NOT_EXIST,
    FOUND
}

class AssetViewModel(private val assetDao: AssetDao) : ViewModel() {

    // Kusa itong mag-uupdate kapag may nabago sa Database (LIFESAVER!)
    val assets: StateFlow<List<Asset>> = assetDao.getAllAssets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var importSummary by mutableStateOf<ImportSummary?>(null)
        private set

    var validationStatus by mutableStateOf(ValidationStatus.INITIAL)
        private set

    var showValidationDialog by mutableStateOf(false)
        private set

    // ----------------------------------------------------------------------------------
    //                                       METHOD / FUNCTION
    //-----------------------------------------------------------------------------------
    fun addAsset(
        assetNumber: String,
        description: String,
        location: String,
        remarks: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newAsset = Asset(
                assetNumber = assetNumber,
                description = description,
                location = location,
                remarks = remarks,
                validate = "Not Found"
            )
            assetDao.insertAsset(newAsset)
        }
    }




    fun validateAsset(assetNo: String) {
        val trimmedNo = assetNo.trim()

        if (trimmedNo.isEmpty()) {
            validationStatus = ValidationStatus.INITIAL
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val asset = assetDao.findAssetByNumber(trimmedNo)

            withContext(Dispatchers.Main) {
                if (asset == null) {
                    validationStatus = ValidationStatus.NOT_EXIST
                } else {
                    val updateAsset = asset.copy(
                        validate = "Found",
                        updatedAt = System.currentTimeMillis()
                    )
                    assetDao.update(updateAsset)
                    validationStatus = ValidationStatus.FOUND
                }
            }
        }
    }

    fun resetValidationStatus() {
        validationStatus = ValidationStatus.INITIAL
    }

    fun updateAsset(asset: Asset) {
        viewModelScope.launch(Dispatchers.IO) {
            assetDao.update(asset)
        }
    }

    fun deleteAsset(asset: Asset) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                assetDao.deleteAsset(asset)
            } catch (e: Exception) {
                Log.e("Delete Error", "Failed to delete Asset", e)
            }
        }
    }

    fun clearImportSummary() {
        importSummary = null
    }

    fun importCsv(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            var totalCount = 0
            var successCount = 0
            var skippedCount = 0
            val validAssets = mutableListOf<Asset>()

            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().useLines { lines ->
                        lines.drop(1).forEach { line ->
                            totalCount++
                            val tokens = line.split(",")
                            if (tokens.size < 3) { skippedCount++; return@forEach }

                            val assetNo = tokens[0].trim()
                            val desc = tokens[1].trim()
                            val loc = tokens[2].trim()
                            val rem = tokens[3].trim()

                            // Check sa Database kung existing na
                            val alreadyExists = assetDao.exists(assetNo)

                            if (assetNo.isNotEmpty() && !alreadyExists) {
                                validAssets.add(Asset(
                                    assetNumber = assetNo,
                                    description = desc,
                                    location = loc,
                                    remarks = rem
                                ))
                                successCount++
                            } else {
                                skippedCount++
                            }
                        }
                    }
                }

                // ETO YUNG IMPORTANTE: Save sa Room
                if (validAssets.isNotEmpty()) {
                    assetDao.insertAll(validAssets)
                }

                // Ipakita ang Summary sa UI
                withContext(Dispatchers.Main) {
                    importSummary = ImportSummary(totalCount, successCount, skippedCount)
                }

            } catch (e: Exception) {
                Log.e("ImportError", "Failed to import CSV", e)
            }
        }
    }

    fun exportToCsv(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val assets = assetDao.getAllAssetsForExport()

                val csvHeader = "asset_number,description,location,remarks,validate\n"

                val csvBody = assets.joinToString(separator = "\n") {asset ->
                    "${asset.assetNumber},${asset.description},${asset.location},${asset.remarks},${asset.validate}"
                }

                val fullCsv = csvHeader + csvBody

                val dateStamp = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(java.util.Date())
                val fileName = "asset_report_$dateStamp.csv"

                val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsFolder, fileName)
                file.writeText(fullCsv)

                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Report Saved to Downloads: ${fileName}", android.widget.Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(context, "Export failed: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun closeDialog() {
        showValidationDialog = false
    }

}