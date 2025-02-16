package com.codewithkael.androidminichatwithwebrtc.remote

import android.util.Log
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.Connected
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.IDLE
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.LookingForMatch
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.OfferedMatch
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.ReceivedMatch
import com.codewithkael.androidminichatwithwebrtc.utils.FirebaseFieldNames
import com.codewithkael.androidminichatwithwebrtc.utils.MatchState
import com.codewithkael.androidminichatwithwebrtc.utils.MiniChatApplication
import com.codewithkael.androidminichatwithwebrtc.utils.MyValueEventListener
import com.codewithkael.androidminichatwithwebrtc.utils.SharedPrefHelper
import com.codewithkael.androidminichatwithwebrtc.utils.SignalDataModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val database: DatabaseReference,
    private val prefHelper: SharedPrefHelper,
    private val gson: Gson
) {
    //  Unify all coroutines into a single CoroutineScope
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun observeUserStatus(callback: (MatchState) -> Unit) {
        coroutineScope.launch {
            removeSelfData()
            updateSelfStatus(StatusDataModel(type = LookingForMatch))

            val userId = prefHelper.getUserId()
            val statusRef = database.child(FirebaseFieldNames.USERS).child(userId)
                .child(FirebaseFieldNames.STATUS)

            statusRef.addValueEventListener(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(StatusDataModel::class.java)?.let { status ->
                        val newState = when (status.type) {
                            LookingForMatch -> MatchState.LookingForMatchState
                            OfferedMatch -> MatchState.OfferedMatchState(status.participant!!)
                            ReceivedMatch -> MatchState.ReceivedMatchState(status.participant!!)
                            IDLE -> MatchState.IDLE
                            Connected -> MatchState.Connected
                            else -> null
                        }

                        newState?.let { callback(it) } ?: coroutineScope.launch {
                            updateSelfStatus(StatusDataModel(type = LookingForMatch))
                            callback(MatchState.LookingForMatchState)
                        }
                    } ?: coroutineScope.launch {
                        updateSelfStatus(StatusDataModel(type = LookingForMatch))
                        callback(MatchState.LookingForMatchState)
                    }
                }
            })
        }
    }

    fun observeIncomingSignals(callback: (SignalDataModel) -> Unit) {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.DATA).addValueEventListener(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    super.onDataChange(snapshot)
                    runCatching {
                        gson.fromJson(snapshot.value.toString(), SignalDataModel::class.java)
                    }.onSuccess {
                        if (it != null) callback(it)
                    }.onFailure {
                        Log.d(MiniChatApplication.TAG, "onDataChange: ${it.message}")
                    }
                }
            })
    }

    suspend fun updateParticipantDataModel(participantId: String, data: SignalDataModel) {
        database.child(FirebaseFieldNames.USERS).child(participantId).child(FirebaseFieldNames.DATA)
            .setValue(gson.toJson(data)).await()
    }

    suspend fun updateSelfStatus(status: StatusDataModel) {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.STATUS).setValue(status)
            .await() // Suspends until Firebase operation completes
    }

    suspend fun updateParticipantStatus(participantId: String, status: StatusDataModel) {
        database.child(FirebaseFieldNames.USERS).child(participantId)
            .child(FirebaseFieldNames.STATUS).setValue(status).await()
    }

    suspend fun findNextMatch() {
        removeSelfData()
        findAvailableParticipant { foundTarget ->
            Log.d(MiniChatApplication.TAG, "findNextMatch: $foundTarget")
            foundTarget?.let { target ->
                database.child(FirebaseFieldNames.USERS).child(target)
                    .child(FirebaseFieldNames.STATUS).setValue(
                        StatusDataModel(
                            participant = prefHelper.getUserId(), type = ReceivedMatch
                        )
                    )

                coroutineScope.launch {
                    updateSelfStatus(StatusDataModel(type = OfferedMatch, participant = target))
                }
            }
        }
    }

    private fun findAvailableParticipant(callback: (String?) -> Unit) {
        database.child(FirebaseFieldNames.USERS).orderByChild("status/type")
            .equalTo(LookingForMatch.name)
            .addListenerForSingleValueEvent(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var foundTarget: String? = null
                    snapshot.children.forEach { childSnapshot ->
                        if (childSnapshot.key != prefHelper.getUserId()) {
                            foundTarget = childSnapshot.key
                            return@forEach
                        }
                    }
                    callback(foundTarget)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    suspend fun removeSelfData() {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.DATA).removeValue().await()
    }

    // Cleanup function to cancel all running coroutines
    fun clear() {
        coroutineScope.cancel()
    }
}
