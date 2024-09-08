package com.example.pharmacistassistant

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pharmacistassistant.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    productViewModel: ProductViewModel,
    scannedBarcode: String,
    lastScanTime: Long,
    onScanButtonClick: () -> Unit
) {
    val searchResults by productViewModel.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }
    var isDropdownVisible by remember { mutableStateOf(false) }
    val selectedProducts by productViewModel.selectedProducts.collectAsState()
    val columnSelection = remember { mutableStateOf(getInitialColumnSelection().toMap()) }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(scannedBarcode, lastScanTime) {
        if (scannedBarcode.isNotEmpty()) {
            query = scannedBarcode
            productViewModel.searchByBarcodeOrTradeName(query)
            isDropdownVisible = true
        }
    }

    Scaffold(
        topBar = {
            TopBarWithSearch(
                query = query,
                onQueryChange = { newQuery ->
                    query = newQuery
                    if (query.isNotEmpty()) {
                        productViewModel.searchByBarcodeOrTradeName(query)
                    }
                    isDropdownVisible = query.isNotEmpty() && searchResults.isNotEmpty()
                },
                isDropdownVisible = isDropdownVisible,
                searchResults = searchResults,
                onDropdownItemSelected = { result ->
                    productViewModel.updateSelectedProducts(selectedProducts + result)
                    isDropdownVisible = false
                }
            )
        },
        floatingActionButton = {
            FloatingActionButtonWithPermission(onClick = onScanButtonClick)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    isDropdownVisible = false
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(stringResource(id = R.string.open_filters))
                }

                Box(modifier = Modifier.weight(1f)) {
                    ScannedDataTable(
                        scannedData = selectedProducts,
                        selectedColumns = columnSelection.value,
                        onProductRemove = { product ->
                            productViewModel.updateSelectedProducts(selectedProducts - product)
                        }
                    )
                }

                val totalCommonsPrice by remember(selectedProducts) {
                    derivedStateOf {
                        selectedProducts.sumOf { it.commonsPrice.trim().toDoubleOrNull() ?: 0.0 }
                    }
                }

                Text(
                    text = stringResource(id = R.string.total_commons_price, totalCommonsPrice),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )

                Button(
                    onClick = {
                        productViewModel.resetSearch()
                        query = ""
                        productViewModel.updateSelectedProducts(emptyList())
                        columnSelection.value = getInitialColumnSelection().toMutableMap()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(stringResource(id = R.string.reset_table))
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            BottomSheetContent(columnSelection)
        }
    }
}
