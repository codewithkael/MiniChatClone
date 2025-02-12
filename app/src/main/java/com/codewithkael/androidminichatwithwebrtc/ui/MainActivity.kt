package com.codewithkael.androidminichatwithwebrtc.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.codewithkael.androidminichatwithwebrtc.ui.theme.AndroidMiniChatWithWebRTCTheme
import com.codewithkael.androidminichatwithwebrtc.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidMiniChatWithWebRTCTheme {
                val viewModel: MainViewModel = hiltViewModel()

            }
        }
    }
}
