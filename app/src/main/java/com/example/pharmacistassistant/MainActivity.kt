package com.example.pharmacistassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter

class MainActivity : ComponentActivity() {

    private lateinit var scanResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var scannedBarcode by mutableStateOf("") // State to hold the scanned barcode result

        // Initialize the launcher to handle the scan result
        scanResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val barcodeResult = result.data?.getStringExtra("BARCODE_RESULT")
                barcodeResult?.let {
                    scannedBarcode = it // Update the state with the scanned barcode
                }
            }
        }

        // Initialize the launcher to request camera permission
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startScanActivity() // Launch the scan activity if the permission is granted
            } else {
                AppLogger.logError(this, "Camera permission denied")
            }
        }

        // Set the content once and use the state to update the UI
        setContent {
            MaterialTheme {
                MainScreen(
                    scannedBarcode = scannedBarcode, // Pass the scanned barcode state
                    onRequestCameraPermission = { requestCameraPermission() }
                )
            }
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startScanActivity()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startScanActivity() {
        val intent = Intent(this, ScanActivity::class.java)
        scanResultLauncher.launch(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    scannedBarcode: String, // Accept the scanned barcode state
    onRequestCameraPermission: () -> Unit
) {
    val context = LocalContext.current
    val scannedData = remember { mutableStateListOf<ProductData>() }
    val allData = remember { readExcelFile(context, "your_excel_file.xlsx") }
    var query by remember { mutableStateOf(scannedBarcode) } // Initialize with scanned barcode
    var searchResults by remember { mutableStateOf(listOf<ProductData>()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Trigger search only when scannedBarcode changes
    LaunchedEffect(scannedBarcode) {
        if (scannedBarcode.isNotEmpty()) {
            query = scannedBarcode
            searchResults = allData.filter {
                it.barcode.contains(query, ignoreCase = true) ||
                        it.tradeName.contains(query, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            // Implement navigation drawer or menu
                        }) {
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { newQuery ->
                        query = newQuery
                        searchResults = allData.filter {
                            it.barcode.contains(query, ignoreCase = true) ||
                                    it.tradeName.contains(query, ignoreCase = true)
                        }
                    },
                    label = { Text(stringResource(id = R.string.scan_button_text)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide() // Hide keyboard on Done action
                    })
                )
                LazyColumn {
                    items(searchResults) { result ->
                        ListItem(
                            headlineContent = { Text(result.tradeName) },
                            supportingContent = { Text(result.barcode) },
                            modifier = Modifier.clickable {
                                scannedData.add(result)
                                searchResults = listOf() // Clear search results after selection
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onRequestCameraPermission) {
                Icon(imageVector = Icons.Filled.Camera, contentDescription = "Scan")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { keyboardController?.hide() }) // Hide keyboard when tapping outside
            }
        ) {
            ScannedDataTable(scannedData)
        }
    }
}

@Composable
fun ScannedDataTable(scannedData: List<ProductData>) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(scrollState)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.barcode), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.trade_name), modifier = Modifier.weight(2f))
            Text(text = stringResource(id = R.string.form), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.dosage), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.size), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.factory), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.commons_price), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.quantity_available), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.wholesale_price), modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.purchase_price), modifier = Modifier.weight(1f))
        }
        scannedData.forEach { data ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)) {
                Text(text = data.barcode, modifier = Modifier.weight(1f))
                Text(text = data.tradeName, modifier = Modifier.weight(2f))
                Text(text = data.form, modifier = Modifier.weight(1f))
                Text(text = data.dosage, modifier = Modifier.weight(1f))
                Text(text = data.size, modifier = Modifier.weight(1f))
                Text(text = data.factory, modifier = Modifier.weight(1f))
                Text(text = data.commonsPrice, modifier = Modifier.weight(1f))
                Text(text = data.quantityAvailable, modifier = Modifier.weight(1f))
                Text(text = data.wholesalePrice, modifier = Modifier.weight(1f))
                Text(text = data.purchasePrice, modifier = Modifier.weight(1f))
            }
        }
    }
}
