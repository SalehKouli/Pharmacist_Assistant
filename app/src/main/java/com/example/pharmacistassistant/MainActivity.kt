package com.example.pharmacistassistant

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
        Log.d("MainActivity", "Scan result received: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedBarcode = result.data?.getStringExtra("SCANNED_BARCODE") ?: ""
            Log.d("MainActivity", "Scanned barcode: $scannedBarcode")
            if (scannedBarcode.isNotEmpty()) {
                updateScannedBarcode(scannedBarcode)
            } else {
                Log.w("MainActivity", "Scanned barcode is null")
            }
        } else {
            Log.w("MainActivity", "Scan cancelled or failed")
        }
    }

    private fun updateScannedBarcode(barcode: String) {
        Log.d("MainActivity", "Updating scanned barcode: $barcode")
        scannedBarcode = barcode
        hasScannedBarcode = true
        productViewModel.searchByBarcodeOrTradeName(barcode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        Log.d("MainActivity", "Initial scannedBarcode: $scannedBarcode")

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
