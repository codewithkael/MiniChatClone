package com.codewithkael.androidminichatwithwebrtc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.codewithkael.androidminichatwithwebrtc.ui.screens.MainScreen
import com.codewithkael.androidminichatwithwebrtc.ui.theme.AndroidMiniChatWithWebRTCTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge drawing
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AndroidMiniChatWithWebRTCTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.systemBars) // Ensures space for top & bottom bars
                ) {
                    MainScreen()
                }
            }
        }
    }
}
