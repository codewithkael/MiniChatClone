package com.codewithkael.androidminichatwithwebrtc.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.codewithkael.androidminichatwithwebrtc.ui.theme.AndroidMiniChatWithWebRTCTheme
import com.codewithkael.androidminichatwithwebrtc.ui.viewmodels.MainViewModel
import com.codewithkael.androidminichatwithwebrtc.utils.MiniChatApplication.Companion.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidMiniChatWithWebRTCTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val matchState = viewModel.matchState.collectAsState()
                Log.d(TAG, "onCreate: ${matchState.value}")
            }
        }
    }
}
