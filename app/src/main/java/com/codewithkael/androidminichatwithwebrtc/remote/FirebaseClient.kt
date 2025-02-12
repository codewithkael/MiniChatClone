package com.codewithkael.androidminichatwithwebrtc.remote

import com.codewithkael.androidminichatwithwebrtc.utils.SharedPrefHelper
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val database: DatabaseReference,
    private val prefHelper: SharedPrefHelper
){

    fun test(){
        database.child(prefHelper.getUserId()).setValue("Hello3")
    }
}