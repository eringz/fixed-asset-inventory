package com.example.fixedassetinventory.viewmodel

// mutalbleStateof Imports

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fixedassetinventory.data.dao.AssetDao
import com.example.fixedassetinventory.data.entity.Asset
import com.itextpdf.text.Document
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
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

    val assets: StateFlow<List<Asset>> = assetDao.getAllAssets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var assetError by mutableStateOf<String?>(null)
        private set
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
        remarks: String,
        onSuccess: () -> Unit
    ) {
        val tNo = assetNumber.trim()
        val tDesc = description.trim()
        val tLoc = location.trim()
        val tRem = remarks.trim()


        if (tNo.isBlank() || tDesc.isBlank() || tLoc.isBlank()) {
            assetError = "All fields are required"
            return
        }


        viewModelScope.launch(Dispatchers.IO) {
            val existing = assetDao.exists(tNo)

            if (existing) {
                withContext(Dispatchers.Main) {
                    assetError = "Asset number must be unique."
                }
                return@launch
            }

            val newAsset = Asset(
                assetNumber = tNo,
                description = tDesc,
                location = tLoc,
                remarks = tRem,
                validate = "Not Found"
            )

            assetDao.insertAsset(newAsset)

            withContext(Dispatchers.Main) {
                assetError = null
                onSuccess()
            }
        }
    }

    fun clearAssetError() { assetError = null }



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

                if (validAssets.isNotEmpty()) {
                    assetDao.insertAll(validAssets)
                }

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

                val downloadsFolder = Environment.getExternalStoragePublicDirectory(
                    DIRECTORY_DOWNLOADS
                )
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

    fun exportToExcel(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val assets = assetDao.getAllAssetsForExport()
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Asset Report")

            val headerRow = sheet.createRow(0)
            val headers = listOf("asset_number", "description", "location", "remarks", "validate")
            headers.forEachIndexed { index, title ->
                headerRow.createCell(index).setCellValue(title)
            }

            assets.forEachIndexed { index, asset ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(asset.assetNumber)
                row.createCell(1).setCellValue(asset.description)
                row.createCell(2).setCellValue(asset.location)
                row.createCell(3).setCellValue(asset.remarks)
                row.createCell(4).setCellValue(asset.validate)
            }

            val dateStamp = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(java.util.Date())
            val fileName = "asset_report_$dateStamp.xlsx"
            val file = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), fileName)

            file.outputStream().use {fos ->
                workbook.write(fos)
                fos.flush()
            }

            workbook.close()

            withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Excel Saved: $fileName", Toast.LENGTH_LONG).show()
            }
        }
    }


    fun exportToPdf(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val assets = assetDao.getAllAssetsForExport()
                val dateStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(java.util.Date())
                val fileName = "asset_report_$dateStamp.pdf"
                val file = File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS), fileName)

                // 1. Initialize Document
                val document = Document()

                // 2. Open Stream
                file.outputStream().use { fos ->

                    PdfWriter.getInstance(document, fos)

                    document.open()

                    val table = PdfPTable(5)
                    table.widthPercentage = 100f

                    // Headers
                    val headers = listOf("Asset No", "Description", "Location", "Remarks", "Status")
                    headers.forEach {
                        table.addCell(Phrase(it))
                    }

                    // Data Rows
                    assets.forEach { asset ->
                        table.addCell(Phrase(asset.assetNumber ?: ""))
                        table.addCell(Phrase(asset.description ?: ""))
                        table.addCell(Phrase(asset.location ?: ""))
                        table.addCell(Phrase(asset.remarks ?: ""))
                        table.addCell(Phrase(asset.validate ?: ""))
                    }

                    document.add(table)


                    document.close()
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "PDF Export Success: $fileName", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("PDF_FINAL_CRASH", "Crash: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Fatal PDF Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun closeDialog() {
        showValidationDialog = false
    }
    companion object {
        fun provideFactory(assetDao: AssetDao): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AssetViewModel(assetDao) as T
                }
            }
    }
}


