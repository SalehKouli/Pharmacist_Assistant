package com.example.pharmacistassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.pharmacistassistant.ui.theme.PharmacistAssistantTheme
import com.example.pharmacistassistant.utils.AppLogger
import com.example.pharmacistassistant.viewmodel.ProductViewModel
import com.example.pharmacistassistant.viewmodel.ProductViewModelFactory

class MainActivity : AppCompatActivity() {

    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(application)
    }

    private var hasScannedBarcode by mutableStateOf(false)
    private var scannedBarcode by mutableStateOf("")

    private val scanBarcodeLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        handleScanResult(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppLogger.logDebug(this, "onCreate called")
        AppLogger.logDebug(this, "scannedBarcode: $scannedBarcode")

        setContent {
            PharmacistAssistantTheme {
                MainScreen(
                    productViewModel = productViewModel,
                    scannedBarcode = scannedBarcode,
                    onScanButtonClick = { checkAndRequestCameraPermission() }
                )
            }
        }
    }

    private fun handleScanResult(result: ActivityResult) {
        AppLogger.logDebug(this, "handleScanResult called")
        if (result.resultCode == RESULT_OK) {
            result.data?.getStringExtra("SCANNED_BARCODE")?.let {
                scannedBarcode = it
                hasScannedBarcode = true
                AppLogger.logDebug(this, "Scanned barcode: $scannedBarcode")
                // Trigger search with the scanned barcode
                productViewModel.searchByBarcodeOrTradeName(scannedBarcode)
            }
        } else {
            hasScannedBarcode = false
            Toast.makeText(this, R.string.no_scan_result, Toast.LENGTH_SHORT).show()
            AppLogger.logDebug(this, "No barcode scanned")
        }
    }

    private fun checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this, ScanActivity::class.java)
            scanBarcodeLauncher.launch(intent)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(this, ScanActivity::class.java)
            scanBarcodeLauncher.launch(intent)
        } else {
            Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show()
        }
    }
}
