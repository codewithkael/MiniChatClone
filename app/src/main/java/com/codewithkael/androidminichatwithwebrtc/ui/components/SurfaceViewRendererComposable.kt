package com.codewithkael.androidminichatwithwebrtc.ui.components

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.SurfaceViewRenderer

@Composable
fun SurfaceViewRendererComposable(
    modifier: Modifier = Modifier,
    onSurfaceReady: (SurfaceViewRenderer) -> Unit,
    message: String? = null // Accept a message string
) {
    if (!message.isNullOrEmpty()) {
        // If the message is not empty, show a black screen with the message
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black), // Black background
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = Color.White, // White text color
                style = TextStyle(fontSize = 18.sp) // Adjust text size as needed
            )
        }
    } else {
        // If the message is empty, show the SurfaceViewRenderer
        Column(
            modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AndroidView(modifier = Modifier
                .fillMaxWidth()
                .weight(1f), factory = { ctx ->
                FrameLayout(ctx).apply {
                    addView(SurfaceViewRenderer(ctx).also {
                        onSurfaceReady.invoke(it)
                    })
                }
            })
        }
    }
}
