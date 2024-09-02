package com.example.pharmacistassistant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.pharmacistassistant.ui.theme.PharmacistAssistantTheme
import com.example.pharmacistassistant.viewmodel.ProductViewModel
import com.example.pharmacistassistant.viewmodel.ProductViewModelFactory
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {

    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(application)
    }

    private var scannedBarcode by mutableStateOf("")

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Log.d("MainActivity", "Scan cancelled")
        } else {
            Log.d("MainActivity", "Scanned barcode: ${result.contents}")
            updateScannedBarcode(result.contents)
        }
    }

    private fun updateScannedBarcode(barcode: String) {
        Log.d("MainActivity", "Updating scanned barcode: $barcode")
        scannedBarcode = barcode
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
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                startBarcodeScanner()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_LONG).show()
            }
            else -> {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun startBarcodeScanner() {
        val options = ScanOptions()
            .setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            .setPrompt(getString(R.string.scan_button_text))
            .setCameraId(0)
            .setBeepEnabled(false)
            .setBarcodeImageEnabled(true)
            .setOrientationLocked(false)

        barcodeLauncher.launch(options)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner()
            } else {
                Toast.makeText(this, R.string.camera_permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}
