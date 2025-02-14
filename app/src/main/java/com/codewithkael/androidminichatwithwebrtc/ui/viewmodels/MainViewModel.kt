package com.codewithkael.androidminichatwithwebrtc.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import com.codewithkael.androidminichatwithwebrtc.remote.FirebaseClient
import com.codewithkael.androidminichatwithwebrtc.utils.MatchState
import com.codewithkael.androidminichatwithwebrtc.webrtc.RTCAudioManager
import com.codewithkael.androidminichatwithwebrtc.webrtc.WebRTCFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseClient: FirebaseClient,
    private val webRTCFactory: WebRTCFactory,
    private val application: Application
) : ViewModel() {

    private val rtcAudioManager by lazy { RTCAudioManager.create(application) }
    var matchState: MutableStateFlow<MatchState> = MutableStateFlow(MatchState.New)
        private set

    init {
        rtcAudioManager.setDefaultAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
    }

    fun permissionsGranted(){
        firebaseClient.observeUserStatus { status ->
            matchState.value = status
        }
        firebaseClient.findNextMatch()
    }

    fun startLocalStream(surface:SurfaceViewRenderer){
        webRTCFactory.prepareLocalStream(surface)
    }

    fun switchCamera() {
        webRTCFactory.switchCamera()
    }
}