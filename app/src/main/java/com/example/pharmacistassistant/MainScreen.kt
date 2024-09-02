package com.example.pharmacistassistant

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.pharmacistassistant.viewmodel.ProductViewModel

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

    LaunchedEffect(selectedProducts) {
        Log.d("MainScreen", "selectedProducts updated. Count: ${selectedProducts.size}")
    }

    val columnSelection = remember { mutableStateOf(getInitialColumnSelection().toMap()) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(scannedBarcode, lastScanTime) {
        Log.d("MainScreen", "LaunchedEffect triggered with scannedBarcode: $scannedBarcode")
        if (scannedBarcode.isNotEmpty()) {
            query = scannedBarcode
            Log.d("MainScreen", "Updating query to: $query")
            productViewModel.searchByBarcodeOrTradeName(query)
            isDropdownVisible = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(columnSelection)
        }
    ) {
        Scaffold(
            topBar = {
                TopBarWithSearch(
                    query = query,
                    onQueryChange = { newQuery ->
                        query = newQuery
                        Log.d("MainScreen", "Query changed to: $query")
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
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isDropdownVisible = false
                    }
            ) {
                MainContent(
                    modifier = Modifier.padding(innerPadding),
                    productViewModel = productViewModel,
                    drawerState = drawerState,
                    selectedProducts = selectedProducts,
                    onSelectedProductsChange = { newProducts ->
                        productViewModel.updateSelectedProducts(newProducts)
                    },
                    columnSelection = columnSelection,
                    onReset = {
                        productViewModel.resetSearch()
                        query = ""
                        productViewModel.updateSelectedProducts(emptyList())
                        columnSelection.value = getInitialColumnSelection().toMutableMap()
                    }
                )
            }
        }
    }
}
