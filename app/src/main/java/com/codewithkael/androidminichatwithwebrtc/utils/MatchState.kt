package com.codewithkael.androidminichatwithwebrtc.utils

sealed class MatchState {
    data object New : MatchState()
    data object LookingForMatch : MatchState()
}