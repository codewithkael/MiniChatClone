package com.codewithkael.androidminichatwithwebrtc.utils

sealed class MatchState {
    data object LookingForMatch : MatchState()
}