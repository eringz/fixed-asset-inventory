package com.example.fixedassetinventory.utils

import android.content.Context
import com.example.fixedassetinventory.data.entity.Asset
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExportService(private val context: Context) {

    fun exportToCsv(assets: List<Asset>): File {
        val file = File(context.cacheDir, "asset_report_${System.currentTimeMillis()}.csv")
        file.bufferedWriter().use {
            out ->
            out.write("Asset No, Description, Location, Remarks, Validate, Date Created\n")

            assets.forEach {
                asset ->
                val no = asset.assetNumber.replace(",", " ")
                val desc = asset.description.replace(",", " ")
                val loc = asset.location.replace(",", " ")
                val rem = asset.remarks.replace(",", " ")

                val line = "$no, $desc, $loc, $rem, ${asset.validate}, ${DateUtils.formatMillis(asset.createdAt)}\n"
                out.write(line)
            }
        }

        return file
    }

    fun exportToExcel(assets: List<Asset>): File {
        val file = File(context.cacheDir, "asset_report_${System.currentTimeMillis()}.xlsx")
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Asset Inventory")

//        val headerRow = sheet.createRow()
        // TODO teamplate

        return file
    }

    fun exportToPDF(assets: List<Asset>) {
        // TODO: For PDF export logic
    }
}