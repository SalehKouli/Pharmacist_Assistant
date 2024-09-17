package com.example.pharmacistassistant

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.EditText
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
import androidx.fragment.app.DialogFragment
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pharmacistassistant.ui.theme.PharmacistAssistantTheme
import com.example.pharmacistassistant.viewmodel.ProductViewModel
import com.example.pharmacistassistant.viewmodel.ProductViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val productViewModel: ProductViewModel by viewModels {
        ProductViewModelFactory(application)
    }

    private var scannedBarcode by mutableStateOf("")
    private var lastScanTime by mutableLongStateOf(0L)
    private lateinit var sharedPreferences: SharedPreferences

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Log.d("MainActivity", "Scan cancelled")
        } else {
            Log.d("MainActivity", "Scanned barcode: ${result.contents}")
            updateScannedBarcode(result.contents)
        }
    }

    private val installPackagesPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkForAppUpdate()
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
                    onScanButtonClick = { checkAndRequestCameraPermission() },
                    sharedPreferences = sharedPreferences
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        Log.d("MainActivity", "Initial scannedBarcode: $scannedBarcode")

        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        setContent {
            PharmacistAssistantTheme {
                MainScreen(
                    productViewModel = productViewModel,
                    scannedBarcode = scannedBarcode,
                    lastScanTime = lastScanTime,
                    onScanButtonClick = { checkAndRequestCameraPermission() },
                    sharedPreferences = sharedPreferences
                )
            }
        }

        checkForAppUpdate()

        if (isFirstLaunch()) {
            showUserInfoDialog()
        }

        setupPeriodicWorkRequest()
    }

    private fun isFirstLaunch(): Boolean {
        val prefs = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
        }
        return isFirstLaunch
    }

    private fun showUserInfoDialog() {
        val dialogFragment = UserInfoDialogFragment()
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, "userInfoDialog")
    }

    private fun setupPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<UserDataWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "userDataSubmission",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
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
            .setOrientationLocked(true)
            .setCaptureActivity(YourPortraitCaptureActivity::class.java)

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
            .url("https://drive.google.com/uc?export=download&id=1v2uRhoo2HytfR0_Aa4r8PXNfglLD7pmO")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainActivity", "Failed to check for updates", e)
                onResult(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("MainActivity", "Unsuccessful response code: ${response.code}")
                    onResult(false, null)
                    return
                }

                response.body?.string()?.let { responseBody ->
                    Log.d("MainActivity", "Received version.json: $responseBody")
                    try {
                        val versionInfo = Gson().fromJson(responseBody, VersionInfo::class.java)
                        if (versionInfo.versionCode > currentVersionCode) {
                            Log.d("MainActivity", "New version available: ${versionInfo.versionCode}")
                            runOnUiThread {
                                onResult(true, versionInfo.apkUrl)
                            }
                        } else {
                            Log.d("MainActivity", "No update needed. Current version: $currentVersionCode")
                            runOnUiThread {
                                onResult(false, null)
                            }
                        }
                    } catch (e: JsonSyntaxException) {
                        Log.e("MainActivity", "JSON parsing error", e)
                        runOnUiThread {
                            onResult(false, null)
                        }
                    }
                } ?: run {
                    Log.e("MainActivity", "Response body is null")
                    runOnUiThread {
                        onResult(false, null)
                    }
                }
            }
        })
    }

    private fun showUpdateDialog(channelUrl: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.update_available))
        builder.setMessage(getString(R.string.update_message))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.go_to_channel)) { _, _ ->
            redirectToChannel(channelUrl)
        }
        builder.show()
    }

    private fun redirectToChannel(channelUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(channelUrl)
        startActivity(intent)
        finishAffinity()
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

    class UserInfoDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_user_info, null)

            val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)
            val locationEditText = view.findViewById<EditText>(R.id.locationEditText)
            val submitButton = view.findViewById<MaterialButton>(R.id.submitButton)

            submitButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val location = locationEditText.text.toString()
                if (username.isNotBlank() && location.isNotBlank()) {
                    saveUserInfo(username, location)
                    dismiss()  // Close the dialog
                } else {
                    Toast.makeText(context, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                }
            }

            builder.setView(view)

            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)  // Prevent dismissing by touching outside
            dialog.setCancelable(false)  // Prevent dismissing by pressing back

            return dialog
        }

        private fun saveUserInfo(username: String, location: String) {
            val userDatabase = UserDatabase(requireContext())
            userDatabase.insertUser(username, location)
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }
}
