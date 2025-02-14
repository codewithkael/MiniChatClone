package com.codewithkael.androidminichatwithwebrtc.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID
import javax.inject.Inject

class SharedPrefHelper @Inject constructor(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "mini_chat_preferences"
        private const val USER_ID_KEY = "user_id_key"
    }

    // Get the USER_ID, if exists, or generate and save it if not
    fun getUserId(): String {
        // Check if USER_ID exists in SharedPreferences
        val userId = sharedPreferences.getString(USER_ID_KEY, null)

        return if (userId.isNullOrEmpty()) {
            // If USER_ID doesn't exist, generate a new one and save it
            val newUserId = UUID.randomUUID().toString().substring(0, 6)
            saveUserId(newUserId)
            newUserId
        } else {
            // If USER_ID exists, return it
//            userId
            "Masoud"
        }
    }

    // Save the USER_ID
    private fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
    }
}
