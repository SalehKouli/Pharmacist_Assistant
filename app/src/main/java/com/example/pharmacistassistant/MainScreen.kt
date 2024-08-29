package com.example.pharmacistassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.pharmacistassistant.viewmodel.ProductViewModel

@Composable
fun MainScreen(
    productViewModel: ProductViewModel,
    scannedBarcode: String,
    onScanButtonClick: () -> Unit
) {
    val searchResults by productViewModel.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }
    var isDropdownVisible by remember { mutableStateOf(false) }
    var selectedProducts by remember { mutableStateOf(listOf<ProductData>()) }
    val columnSelection = remember { mutableStateOf(getInitialColumnSelection().toMap()) }

    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val intent = Intent(context, ScanActivity::class.java)
                context.startActivity(intent)
            }
        }
    )

    LaunchedEffect(scannedBarcode) {
        Log.d("MainScreen", "LaunchedEffect triggered with scannedBarcode: $scannedBarcode")
        if (scannedBarcode.isNotEmpty()) {
            query = scannedBarcode
            Log.d("MainScreen", "Updating query to: $query")
            productViewModel.searchByBarcodeOrTradeName(query)
        }
    }

    fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(context, ScanActivity::class.java)
            context.startActivity(intent)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(columnSelection) // Drawer should only contain the filtering checklist
        }
    ) {
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
                        selectedProducts = selectedProducts + result
                        isDropdownVisible = false
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButtonWithPermission(onClick = { checkAndRequestCameraPermission() })
            }
        ) { innerPadding ->
            MainContent(
                modifier = Modifier.padding(innerPadding),
                productViewModel = productViewModel, // Pass productViewModel here
                drawerState = drawerState,
                selectedProducts = selectedProducts,
                columnSelection = columnSelection,
                onReset = {
                    productViewModel.resetSearch()
                    query = ""
                    selectedProducts = emptyList()
                    columnSelection.value = getInitialColumnSelection().toMutableMap()
                }
            )
        }
    }
}
