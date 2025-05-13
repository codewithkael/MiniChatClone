package com.codewithkael.androidminichatwithwebrtc.remote

import android.util.Log
import com.codewithkael.androidminichatwithwebrtc.cryptography.CryptoSessionImpl
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.Connected
import com.codewithkael.androidminichatwithwebrtc.remote.StatusDataModelTypes.IDLE
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.security.PublicKey
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
    private var participantPublicKey: PublicKey? = null
    private val cryptoSession = CryptoSessionImpl()
    private val keyPair = cryptoSession.getRSAService().generateRSAKeyPair(2048)
    private val keyPairString = cryptoSession.getRSAService().convertKeyPairToBase64String(keyPair)

    fun observeUserStatus(callback: (MatchState) -> Unit) {
        coroutineScope.launch {
            removeSelfData()
            updateSelfStatus(StatusDataModel(type = LookingForMatch))
            updateSelfPublicKey(keyPairString.first)
            val userId = prefHelper.getUserId()
            val statusRef = database.child(FirebaseFieldNames.USERS).child(userId)
                .child(FirebaseFieldNames.STATUS)

            statusRef.addValueEventListener(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(StatusDataModel::class.java)?.let { status ->
                        coroutineScope.launch {
                            val newState = when (status.type) {
                                LookingForMatch -> MatchState.LookingForMatchState
                                OfferedMatch -> {
                                    getUserPublicKey(status.participant!!)?.let {
                                        participantPublicKey =
                                            cryptoSession.getRSAService().base64ToPublicKey(it)
                                    }
                                    MatchState.OfferedMatchState(status.participant)
                                }

                                ReceivedMatch -> {
                                    getUserPublicKey(status.participant!!)?.let {
                                        participantPublicKey =
                                            cryptoSession.getRSAService().base64ToPublicKey(it)
                                    }
                                    MatchState.ReceivedMatchState(status.participant)
                                }

                                IDLE -> MatchState.IDLE
                                Connected -> MatchState.Connected
                                else -> null
                            }

                            newState?.let {
                                callback(it)
                            } ?: run {
                                updateSelfStatus(StatusDataModel(type = LookingForMatch))
                                callback(MatchState.LookingForMatchState)
                            }
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
                        coroutineScope.launch {
                            if (it?.data != null) {
                                val participantAesKeyString = cryptoSession.getRSAService()
                                    .decryptText(it.encryptedAesKey!!, keyPair.private)
                                val decryptedAesKey = cryptoSession.getAESService()
                                    .convertStringToKey(participantAesKeyString!!)
                                val decryptedData = cryptoSession.getAESService()
                                    .decryptText(it.data.toString(), decryptedAesKey)
                                callback(it.copy(data = decryptedData))
                            }
                        }

                    }.onFailure {
                        Log.d(TAG, "onDataChange: ${it.message}")
                    }
                }
            })
    }

    suspend fun updateParticipantDataModel(participantId: String, data: SignalDataModel) {
        val aesKey = cryptoSession.getAESService().generateKey(256)
        val aesKeyString = cryptoSession.getAESService().convertKeyToString(aesKey)
        val encryptedAesKey = participantPublicKey?.let {
            cryptoSession.getRSAService().encryptText(
                aesKeyString, it
            )
        }
        val encryptedData = cryptoSession.getAESService().encryptText(data.data.toString(), aesKey)
        database.child(FirebaseFieldNames.USERS).child(participantId).child(FirebaseFieldNames.DATA)
            .setValue(
                gson.toJson(
                    data.copy(
                        encryptedAesKey = encryptedAesKey, data = encryptedData
                    )
                )
            ).await()
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
            Log.d(TAG, "findNextMatch: $foundTarget")
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

    private suspend fun updateSelfPublicKey(publicKey: String) {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.PUBLIC_KEY).setValue(publicKey).await()
    }

    suspend fun getUserPublicKey(userId: String): String? {
        return try {
            val snapshot = database.child(FirebaseFieldNames.USERS).child(userId)
                .child(FirebaseFieldNames.PUBLIC_KEY).get().await()
            snapshot.getValue(String::class.java)
        } catch (e: Exception) {
            Log.e(
                TAG, "getUserPublicKey: Failed to fetch public key for user $userId", e
            )
            null
        }
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
