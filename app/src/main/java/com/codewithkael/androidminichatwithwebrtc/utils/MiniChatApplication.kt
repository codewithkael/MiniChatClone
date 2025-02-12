package com.codewithkael.androidminichatwithwebrtc.utils

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MiniChatApplication : Application() {
    companion object {
        const val TAG = "MiniChat"
    }
}