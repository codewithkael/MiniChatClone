package com.codewithkael.androidminichatwithwebrtc.remote

data class StatusDataModel(
    val participant: String? = null,
    val type: StatusDataModelTypes? = null,
    val participantKey: String? = null,
)

enum class StatusDataModelTypes {
    IDLE, LookingForMatch, OfferedMatch, ReceivedMatch, Connected
}