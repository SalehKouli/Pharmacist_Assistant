package com.example.pharmacistassistant

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ScannedDataTable(scannedData: List<ProductData>, selectedColumns: Map<Int, Boolean>) {
    val horizontalScrollState = rememberScrollState()

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .horizontalScroll(horizontalScrollState)
    ) {
        // Header row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                if (selectedColumns[R.string.barcode] == true) {
                    Text(
                        text = stringResource(id = R.string.barcode),
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.trade_name] == true) {
                    Text(
                        text = stringResource(id = R.string.trade_name),
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.form] == true) {
                    Text(
                        text = stringResource(id = R.string.form),
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.dosage] == true) {
                    Text(
                        text = stringResource(id = R.string.dosage),
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.size] == true) {
                    Text(
                        text = stringResource(id = R.string.size),
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.factory] == true) {
                    Text(
                        text = stringResource(id = R.string.factory),
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.commons_price] == true) {
                    Text(
                        text = stringResource(id = R.string.commons_price),
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.quantity_available] == true) {
                    Text(
                        text = stringResource(id = R.string.quantity_available),
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.wholesale_price] == true) {
                    Text(
                        text = stringResource(id = R.string.wholesale_price),
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.purchase_price] == true) {
                    Text(
                        text = stringResource(id = R.string.purchase_price),
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Data rows
        items(scannedData) { data ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                if (selectedColumns[R.string.barcode] == true) {
                    Text(
                        text = data.barcode,
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.trade_name] == true) {
                    Text(
                        text = data.tradeName,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.form] == true) {
                    Text(
                        text = data.form,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.dosage] == true) {
                    Text(
                        text = data.dosage,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.size] == true) {
                    Text(
                        text = data.size,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.factory] == true) {
                    Text(
                        text = data.factory,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.commons_price] == true) {
                    Text(
                        text = data.commonsPrice,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.quantity_available] == true) {
                    Text(
                        text = data.quantityAvailable,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.wholesale_price] == true) {
                    Text(
                        text = data.wholesalePrice,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
                if (selectedColumns[R.string.purchase_price] == true) {
                    Text(
                        text = data.purchasePrice,
                        modifier = Modifier
                            .width(150.dp)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}
