package com.codewithkael.androidminichatwithwebrtc.remote

data class StatusDataModel(
    val participant: String? = null,
    val type: StatusDataModelTypes? = null,
)

enum class StatusDataModelTypes {
    IDLE, LookingForMatch, OfferedMatch, ReceivedMatch, Connected
}