package com.example.pharmacistassistant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.CaptureActivity

class ScanActivity : AppCompatActivity() {

    private lateinit var barcodeScannerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the launcher for barcode scanner
        barcodeScannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultText = result.data?.getStringExtra("SCAN_RESULT")
                if (resultText != null) {
                    val resultIntent = Intent().apply {
                        putExtra("BARCODE_RESULT", resultText)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                } else {
                    showToast(getString(R.string.no_scan_result))
                }
            } else {
                showToast(getString(R.string.scan_failed))
            }
            finish()  // Ensure the activity is finished after handling the result
        }

        startBarcodeScanner()
    }

    private fun startBarcodeScanner() {
        val intent = Intent(this, CaptureActivity::class.java)
        barcodeScannerLauncher.launch(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
