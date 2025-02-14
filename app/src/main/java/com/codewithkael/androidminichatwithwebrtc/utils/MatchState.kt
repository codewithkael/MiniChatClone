package com.codewithkael.androidminichatwithwebrtc.utils

sealed class MatchState {
    data object NewState : MatchState()
    data object IDLE : MatchState()
    data object LookingForMatchState : MatchState()
    data class OfferedMatchState(val participant:String) : MatchState()
    data class ReceivedMatchState(val participant:String) : MatchState()
    data object Connected : MatchState()
}