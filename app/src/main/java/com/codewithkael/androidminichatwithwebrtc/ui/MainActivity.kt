package com.codewithkael.androidminichatwithwebrtc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.codewithkael.androidminichatwithwebrtc.ui.screens.MainScreen
import com.codewithkael.androidminichatwithwebrtc.ui.theme.AndroidMiniChatWithWebRTCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidMiniChatWithWebRTCTheme {
                MainScreen()
            }
        }
    }
}
