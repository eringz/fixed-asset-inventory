package com.example.fixedassetinventory.utils

import android.content.Context
import android.net.Uri
import com.example.fixedassetinventory.data.entity.Asset
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class ImportService(private val context: Context) {

    suspend fun parseCsv(uri: Uri): List<Asset> {

        val assets = mutableListOf<Asset>()
        context.contentResolver.openInputStream(uri)?.use {
            inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use {
                reader ->
                reader.readLine()
                var line: String? = reader.readLine()
                while(line != null) {
                    val tokens = line.split(",")
                    if(tokens.size >= 3) {
                        assets.add(mapToAsset(tokens[0], tokens[1], tokens[2], tokens.getOrNull(3)))
                    }
                    line = reader.readLine()
                }
            }
        }

        return assets
    }

    suspend fun parseExcel(uri: Uri): List<Asset> {
        val assets = mutableListOf<Asset>()

        context.contentResolver.openInputStream(uri)?.use {
            inputStream ->
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)

            for (row in sheet) {
                if (row.rowNum == 0) continue

                val assetNo = row.getCell(0)?.toString() ?: ""
                val desc = row.getCell(1)?.toString() ?: ""
                val loc = row.getCell(2)?.toString() ?: ""
                val rem = row.getCell(3)?.toString() ?: ""

                if (assetNo.isNotEmpty()) {
                    assets.add(mapToAsset(assetNo, desc, loc, rem))
                }
            }
            workbook.close()
        }
        return assets
    }

    private fun mapToAsset(no: String, desc: String, loc: String, rem: String?): Asset {
        val currentTime = System.currentTimeMillis()
        return Asset(
            assetNumber = no.trim(),
            description = desc.trim(),
            location = loc.trim(),
            remarks = rem?.trim() ?: "",
            validate = "Not Found",
            createdAt = currentTime,
            updatedAt = currentTime
        )
    }
}