package com.example.pharmacistassistant

import android.content.Context
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

// Data class to hold the product information
data class ProductData(
    val barcode: String,
    val tradeName: String,
    val form: String,
    val dosage: String,
    val size: String,
    val factory: String,
    val commonsPrice: String,
    val quantityAvailable: String,
    val wholesalePrice: String,
    val purchasePrice: String
)

// Function to read the Excel file from assets and parse it into a list of ProductData
fun readExcelFile(context: Context, fileName: String): List<ProductData> {
    val productList = mutableListOf<ProductData>()
    val dataFormatter = DataFormatter() // Formatter to handle different cell types

    try {
        // Access the assets folder to get the Excel file
        val assetManager = context.assets
        val inputStream: InputStream = assetManager.open(fileName)

        // Create a workbook using Apache POI
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0) // Assuming the data is in the first sheet

        // Loop through each row in the sheet
        for (row in sheet) {
            if (row.rowNum == 0) continue // Skip the header row

            // Extracting cell data with formatting to handle numbers as text
            val purchasePrice = dataFormatter.formatCellValue(row.getCell(0)) // Column A
            val wholesalePrice = dataFormatter.formatCellValue(row.getCell(1)) // Column B
            val quantityAvailable = dataFormatter.formatCellValue(row.getCell(2)) // Column C
            val commonsPrice = dataFormatter.formatCellValue(row.getCell(3)) // Column D
            val factory = dataFormatter.formatCellValue(row.getCell(4)) // Column E
            val size = dataFormatter.formatCellValue(row.getCell(5)) // Column F
            val dosage = dataFormatter.formatCellValue(row.getCell(6)) // Column G
            val form = dataFormatter.formatCellValue(row.getCell(7)) // Column H
            val tradeName = dataFormatter.formatCellValue(row.getCell(8)) // Column I
            val barcode = dataFormatter.formatCellValue(row.getCell(9)) // Column J (barcode as text)

            // Create a ProductData object and add it to the list
            val productData = ProductData(
                barcode = barcode,
                tradeName = tradeName,
                form = form,
                dosage = dosage,
                size = size,
                factory = factory,
                commonsPrice = commonsPrice,
                quantityAvailable = quantityAvailable,
                wholesalePrice = wholesalePrice,
                purchasePrice = purchasePrice
            )

            productList.add(productData)
        }

        // Close the workbook to free up resources
        workbook.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return productList
}
