package com.codewithkael.androidminichatwithwebrtc.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.codewithkael.androidminichatwithwebrtc.remote.FirebaseClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseClient: FirebaseClient
) : ViewModel() {

    init {
        firebaseClient.test()
    }
}