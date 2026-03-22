package com.example.fixedassetinventory.utils

import com.example.fixedassetinventory.data.entity.Asset

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.widget.Toast


import java.io.FileOutputStream
import java.io.File

import org.apache.poi.xssf.usermodel.XSSFWorkbook



import android.graphics.Paint
import android.graphics.Canvas
import com.itextpdf.text.Document as ITextDocument
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter

import android.graphics.Color
import android.util.Log.e


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

    suspend fun exportToExcel(assets: List<Asset>): File? = withContext(Dispatchers.IO) {
        try {

            val file = File(context.cacheDir, "asset_report_${System.currentTimeMillis()}.xlsx")
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Asset Inventory")

            val headerRow = sheet.createRow(0)
            val headers = arrayOf("Asset No", "Description", "Location", "Remarks", "Validate", "Date Created")

            headers.forEachIndexed {
                    index, title ->
                headerRow.createCell(index).setCellValue(title)
            }

            assets.forEachIndexed {
                    index, asset ->
                val row = sheet.createRow(index + 1)

                row.createCell(0).setCellValue(asset.assetNumber)
                row.createCell(1).setCellValue(asset.description)
                row.createCell(2).setCellValue(asset.location)
                row.createCell(3).setCellValue(asset.remarks)
                row.createCell(4).setCellValue(asset.validate)
                row.createCell(5).setCellValue(DateUtils.formatMillis(asset.createdAt))

            }

            sheet.setColumnWidth(1, 10000)
            sheet.setColumnWidth(3, 8000)


            FileOutputStream(file).use {
                    out ->
                workbook.write(out)
//            out.flush()
            }
            workbook.close()

            return@withContext file

        } catch (e: Exception) {
            e.printStackTrace()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    suspend fun exportToPDF(assets: List<Asset>): File? = withContext(Dispatchers.IO) {
        val file = File(context.cacheDir, "asset_report_${System.currentTimeMillis()}.pdf")
        val document = ITextDocument()
        try {
            FileOutputStream(file).use { fos ->
                PdfWriter.getInstance(document, fos)
                document.open()

                val table = PdfPTable(6)
                table.widthPercentage = 100f
                val headers = listOf("Asset No", "Description", "Location", "Remarks", "Validate", "Date Created")
                headers.forEach {
                    table.addCell(Phrase(it))
                }

                assets.forEach { asset ->
                    table.addCell(Phrase(asset.assetNumber))
                    table.addCell(Phrase(asset.description))
                    table.addCell(Phrase(asset.location))
                    table.addCell(Phrase(asset.remarks))
                    table.addCell(Phrase(asset.validate))
                    table.addCell(Phrase(DateUtils.formatMillis(asset.createdAt)))
                }

                document.add(table)
                document.close()
            }
            return@withContext file
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Fatal PDF Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            null
        }
    }
}