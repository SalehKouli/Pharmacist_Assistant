package com.example.pharmacistassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.pharmacistassistant.ui.theme.PharmacistAssistantTheme
import com.example.pharmacistassistant.viewmodel.ProductViewModel
import com.example.pharmacistassistant.viewmodel.ProductViewModelFactory
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(application)
    }

    private var scannedBarcode by mutableStateOf("")
    private var lastScanTime by mutableLongStateOf(0L)

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Log.d("MainActivity", "Scan cancelled")
        } else {
            Log.d("MainActivity", "Scanned barcode: ${result.contents}")
            updateScannedBarcode(result.contents)
        }
    }

    // Register for result when requesting the install packages permission
    private val installPackagesPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkForAppUpdate() // Retry update check if the permission is granted
    }

    private fun updateScannedBarcode(barcode: String) {
        Log.d("MainActivity", "Updating scanned barcode: $barcode")
        scannedBarcode = barcode
        lastScanTime = System.currentTimeMillis()
        setContent {
            PharmacistAssistantTheme {
                MainScreen(
                    productViewModel = productViewModel,
                    scannedBarcode = scannedBarcode,
                    lastScanTime = lastScanTime,
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
                    lastScanTime = lastScanTime,
                    onScanButtonClick = { checkAndRequestCameraPermission() }
                )
            }
        }

        // Check for app updates
        checkForAppUpdate()
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

    private fun checkForAppUpdate() {
        val currentVersionCode = AppConstants.VERSION_CODE

        checkForUpdate(currentVersionCode) { isUpdateAvailable, channelUrl ->
            if (isUpdateAvailable && channelUrl != null) {
                showUpdateDialog(channelUrl)
            }
        }
    }

    private fun checkForUpdate(currentVersionCode: Int, onResult: (Boolean, String?) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/SalehKouli/Pharmacist_Assistant/master/version.json") // Replace with your actual hosted version file URL
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Failed to check for updates", e)
                onResult(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    try {
                        val versionInfo = Gson().fromJson(responseBody, VersionInfo::class.java)
                        if (versionInfo.versionCode > currentVersionCode) {
                            onResult(true, versionInfo.apkUrl)
                        } else {
                            onResult(false, null)
                        }
                    } catch (e: JsonSyntaxException) {
                        Log.e("MainActivity", "JSON parsing error", e)
                        onResult(false, null)
                    }
                }
            }
        })
    }

    private fun showUpdateDialog(channelUrl: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Available")
        builder.setMessage("A new version of the app is available. Please visit our Telegram channel to update.")
        builder.setCancelable(false) // Make the dialog not cancelable
        builder.setPositiveButton("Go to Channel") { _, _ ->
            if (!packageManager.canRequestPackageInstalls()) {
                requestInstallPackagesPermission()
            } else {
                redirectToChannel(channelUrl)
            }
        }
        builder.show()
    }

    private fun redirectToChannel(channelUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(channelUrl)
        startActivity(intent)
        finish() // Close the app after redirecting to the channel
    }

    private fun requestInstallPackagesPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:$packageName")
        installPackagesPermissionLauncher.launch(intent)
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
