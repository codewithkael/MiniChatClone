package com.codewithkael.androidminichatwithwebrtc.remote

import android.util.Log
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.LookingForMatch
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.OfferedMatch
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.ReceivedMatch
import com.codewithkael.androidminichatwithwebrtc.utils.FirebaseFieldNames
import com.codewithkael.androidminichatwithwebrtc.utils.MatchState
import com.codewithkael.androidminichatwithwebrtc.utils.MiniChatApplication.Companion.TAG
import com.codewithkael.androidminichatwithwebrtc.utils.MyValueEventListener
import com.codewithkael.androidminichatwithwebrtc.utils.SharedPrefHelper
import com.codewithkael.androidminichatwithwebrtc.utils.SignalDataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val database: DatabaseReference,
    private val prefHelper: SharedPrefHelper,
    private val gson: Gson
) {

    fun observeUserStatus(callback: (MatchState) -> Unit) {
        removeSelfData()
        updateSelfStatus(
            StatusDataModel(
                type = LookingForMatch
            )
        )
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.STATUS)
            .addValueEventListener(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val status = snapshot.getValue(StatusDataModel::class.java)
                    status?.let {
                        when (status.type) {
                            LookingForMatch -> {
                                callback(MatchState.LookingForMatchState)
                            }

                            null -> {
                                updateSelfStatus(StatusDataModel(type = LookingForMatch))
                                callback(MatchState.LookingForMatchState)
                            }

                            OfferedMatch -> {
                                callback(MatchState.OfferedMatchState(status.participant!!))
                            }

                            ReceivedMatch -> {
                                callback(MatchState.ReceivedMatchState(status.participant!!))
                            }
                        }
                    } ?: let {
                        updateSelfStatus(
                            StatusDataModel(
                                type = LookingForMatch
                            )
                        )
                        callback(MatchState.LookingForMatchState)
                    }
                }
            })
    }

    fun observeIncomingSignals(callback: (SignalDataModel) -> Unit) {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.DATA).addValueEventListener(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    super.onDataChange(snapshot)
                    runCatching {
                        gson.fromJson(snapshot.value.toString(), SignalDataModel::class.java)
                    }.onSuccess {
                        if (it!=null){
                            callback(it)
                        }
                    }.onFailure {
                        Log.d(TAG, "onDataChange: ${it.message}")
                    }

                }
            })
    }

    fun updateParticipantDataModel(participantId: String, data: SignalDataModel) {
        database.child(FirebaseFieldNames.USERS).child(participantId).child(FirebaseFieldNames.DATA)
            .setValue(gson.toJson(data))
    }

    fun updateSelfStatus(status: StatusDataModel) {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.STATUS).setValue(status)
    }


    fun findNextMatch() {
        removeSelfData()
        findAvailableParticipant { foundTarget ->
            Log.d(TAG, "findNextMatch: $foundTarget")
            foundTarget?.let { target ->
                database.child(FirebaseFieldNames.USERS).child(target)
                    .child(FirebaseFieldNames.STATUS).setValue(
                        StatusDataModel(
                            participant = prefHelper.getUserId(), type = ReceivedMatch
                        )
                    )
                updateSelfStatus(StatusDataModel(type = OfferedMatch, participant = target))
                //start webrtc connection
            }
        }
    }

    private fun findAvailableParticipant(callback: (String?) -> Unit) {
        // Query the database to find participants with status "LookingForMatch"
        database.child(FirebaseFieldNames.USERS)
            .orderByChild("status/type")  // Adjust the path based on your actual data structure
            .equalTo(LookingForMatch.name)
            .addListenerForSingleValueEvent(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Iterate over all children to find a valid participant
                    var foundTarget: String? = null
                    snapshot.children.forEach { childSnapshot ->
                        if (childSnapshot.key != prefHelper.getUserId()) {
                            foundTarget = childSnapshot.key
                            return@forEach // Break the loop when a valid match is found
                        }
                    }
                    callback(foundTarget)  // Pass the found participant or null if none was found
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error (optional)
                    Log.e(TAG, "Database query cancelled", error.toException())
                    callback(null)  // Return null in case of an error
                }
            })
    }

    private fun removeSelfData() {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.DATA).removeValue()
    }
}