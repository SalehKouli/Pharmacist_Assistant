package com.example.pharmacistassistant

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pharmacistassistant.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    productViewModel: ProductViewModel,
    scannedBarcode: String,
    lastScanTime: Long,
    onScanButtonClick: () -> Unit,
    sharedPreferences: SharedPreferences
) {
    val searchResults by productViewModel.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }
    var isDropdownVisible by remember { mutableStateOf(false) }
    val selectedProducts by productViewModel.selectedProducts.collectAsState()
    val columnSelection = remember {
        mutableStateOf(
            getInitialColumnSelection().mapValues { (key, _) ->
                sharedPreferences.getBoolean(key.toString(), true)
            }
        )
    }
    var showBottomSheet by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(scannedBarcode, lastScanTime) {
        if (scannedBarcode.isNotEmpty()) {
            query = scannedBarcode
            productViewModel.searchByBarcodeOrTradeName(query)
            isDropdownVisible = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(stringResource(R.string.settings), modifier = Modifier.padding(16.dp))
                HorizontalDivider()
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
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
                    },
                    onNavigationClick = { // Handle navigation icon click
                        scope.launch {
                            drawerState.open() }}
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
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            BottomSheetContent(columnSelection, sharedPreferences)
        }
    }
}
