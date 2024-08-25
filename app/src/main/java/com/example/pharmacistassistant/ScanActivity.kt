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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBarcodeScanner()
    }

    private fun startBarcodeScanner() {
        val intent = Intent(this, CaptureActivity::class.java)
        startActivityForResult(intent, BARCODE_SCANNER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == BARCODE_SCANNER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val resultText = data.getStringExtra("SCAN_RESULT")
                if (resultText != null) {
                    val resultIntent = Intent().apply {
                        putExtra("BARCODE_RESULT", resultText)
                    }
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()  // Finish the activity once the result is set
                } else {
                    showToast(getString(R.string.no_scan_result))
                    finish()  // Finish the activity even if there's no result to avoid looping
                }
            } else {
                showToast(getString(R.string.scan_failed))
                finish()  // Finish the activity if scan failed
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val BARCODE_SCANNER_REQUEST_CODE = 1001
    }
}