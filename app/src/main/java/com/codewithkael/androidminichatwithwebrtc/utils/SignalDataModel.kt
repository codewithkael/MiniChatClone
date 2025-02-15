package com.codewithkael.androidminichatwithwebrtc.utils

data class SignalDataModel(
    val type:SignalDataModelTypes?=null,
    val data:String?=null
)

enum class SignalDataModelTypes {
    OFFER,ANSWER,ICE,CHAT
}
