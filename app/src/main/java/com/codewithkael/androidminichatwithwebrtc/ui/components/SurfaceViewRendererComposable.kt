package com.codewithkael.androidminichatwithwebrtc.ui.components

import android.widget.FrameLayout
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.webrtc.SurfaceViewRenderer

@Composable
fun SurfaceViewRendererComposable(
    modifier: Modifier = Modifier, onSurfaceReady: (SurfaceViewRenderer) -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SurfaceViewRenderer
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
