package com.codewithkael.androidminichatwithwebrtc.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.codewithkael.androidminichatwithwebrtc.R
import com.codewithkael.androidminichatwithwebrtc.ui.components.SurfaceViewRendererComposable
import com.codewithkael.androidminichatwithwebrtc.ui.viewmodels.MainViewModel
import com.codewithkael.androidminichatwithwebrtc.utils.MatchState

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    val matchState = viewModel.matchState.collectAsState()
    val context = LocalContext.current

    // Permission request launcher
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (!permissions.all { it.value }) {
            Toast.makeText(
                context, "Camera and Microphone permissions are required", Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.permissionsGranted()
        }
    }

    // Launch permission request effect
    LaunchedEffect(Unit) {
        requestPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA)) // Background color

    ) {
        // Top video screen (large screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2.5f)
        ) {
            SurfaceViewRendererComposable(
                modifier = Modifier.fillMaxSize(),
                onSurfaceReady = { renderer ->
                    // viewModel.setLargeScreenRenderer(renderer)
                })
        }
        Row(
            Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(8.dp)
        ) {
            //chat section here
            Text(text = "Chat")

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (matchState.value == MatchState.LookingForMatch) {
                Box(
                    modifier = Modifier
                        .weight(1f) // This will make the Box take up 1f of the available space
                        .padding(3.dp) // Padding around the Box (this ensures there’s space for the switch camera button)
                ) {
                    // Surface that fills the entire Box
                    SurfaceViewRendererComposable(modifier = Modifier.fillMaxSize(), // This makes the SurfaceViewRenderer fill the available space within the Box
                        onSurfaceReady = { renderer ->
                            viewModel.startLocalStream(renderer) // Start the local stream when SurfaceViewRenderer is ready
                        })

                    // Switch Camera Button at the bottom left
                    IconButton(
                        onClick = {
                            viewModel.switchCamera()
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd) // Align it to the bottom-right of the Box
                            .padding(3.dp)
                            .size(30.dp)
                       
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_switch_camera),
                            contentDescription = "Switch Camera",
                            tint = Color(0xE4E5E5E5),
                            modifier = Modifier.size(30.dp)
                                .background(color= Color(0x465B5B5B), shape = RoundedCornerShape(5.dp))
                                .padding(5.dp)

                        )
                    }

                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(value = "",
                        onValueChange = {},
                        label = { Text("Type your message") },
                        modifier = Modifier.weight(7f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color(0xFFA4BAD1), // Custom color for label when focused
                            unfocusedTextColor = Color(0xFFA4BAD1), // Custom color for label when not focused
                            focusedContainerColor = Color.White, // Custom color for container when focused
                            unfocusedContainerColor = Color.White, // Custom color for container when not focused
                        )
                    )

                    IconButton(onClick = { /* Handle record action */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color(0xFFA4BAD1),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = { /* Handle record action */ },
                        Modifier.weight(5f),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(
                                0xFFC294A4
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Stop",
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)

                        )
                    }

                    Spacer(modifier = Modifier.weight(0.25f))

                    IconButton(
                        onClick = { /* Handle play action */ },
                        Modifier.weight(5f),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFA4BAD1)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }

        }

        // Footer for message input and action buttons

        // Footer text - rules and notice
        Text(
            text = "By using this videchat you agree with our rules. Rules violators will be banned. Try to keep your face visible in camera frame.",
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(16.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black
        )
    }
}
