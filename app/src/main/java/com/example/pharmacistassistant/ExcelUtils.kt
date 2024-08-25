package com.example.pharmacistassistant

import android.content.Context
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import org.apache.poi.ss.usermodel.CellType

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

            // Extracting cell data based on the column order
            val purchasePrice = row.getCell(0).toString() // Column A
            val wholesalePrice = row.getCell(1).toString() // Column B
            val quantityAvailable = row.getCell(2).toString() // Column C
            val commonsPrice = row.getCell(3).toString() // Column D
            val factory = row.getCell(4).toString() // Column E
            val size = row.getCell(5).toString() // Column F
            val dosage = row.getCell(6).toString() // Column G
            val form = row.getCell(7).toString() // Column H
            val tradeName = row.getCell(8).toString() // Column I

            // Read barcode as a string to prevent scientific notation
            val barcode = if (row.getCell(9).cellType == CellType.STRING) {
                row.getCell(9).stringCellValue
            } else {
                // Convert numeric value to string without scientific notation
                row.getCell(9).numericCellValue.toLong().toString() // Convert to Long first to avoid scientific notation
            }

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
