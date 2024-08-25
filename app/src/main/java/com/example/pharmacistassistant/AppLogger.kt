package com.example.pharmacistassistant

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object AppLogger {
    private const val TAG = "AppLogger"
    private const val LOG_FILE_NAME = "app_log.txt"

    private fun getLogFilePath(context: Context): String {
        val logDir = context.getExternalFilesDir(null)
        val logFile = File(logDir, LOG_FILE_NAME)
        return logFile.absolutePath
    }

    fun logDebug(context: Context, message: String) {
        val logFilePath = getLogFilePath(context)
        writeLogToFile(logFilePath, "DEBUG: $message")
        Log.d(TAG, message)
    }

    fun logError(context: Context, message: String, throwable: Throwable? = null) {
        val logFilePath = getLogFilePath(context)
        val fullMessage = "ERROR: $message\n${throwable?.let { Log.getStackTraceString(it) }}"
        writeLogToFile(logFilePath, fullMessage)
        Log.e(TAG, message, throwable)
    }

    private fun writeLogToFile(filePath: String, message: String) {
        try {
            FileWriter(filePath, true).use { writer ->
                writer.append("[${getCurrentTimestamp()}] $message\n")
                writer.flush()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write log to file", e)
        }
    }

    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
