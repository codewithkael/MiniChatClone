package com.codewithkael.androidminichatwithwebrtc.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.codewithkael.androidminichatwithwebrtc.remote.FirebaseClient
import com.codewithkael.androidminichatwithwebrtc.utils.MatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseClient: FirebaseClient
) : ViewModel() {

    var matchState: MutableStateFlow<MatchState> = MutableStateFlow(MatchState.LookingForMatch)
        private set

    init {
        firebaseClient.observeUserStatus { status ->
            matchState.value = status
        }
        firebaseClient.findNextMatch()
    }
}