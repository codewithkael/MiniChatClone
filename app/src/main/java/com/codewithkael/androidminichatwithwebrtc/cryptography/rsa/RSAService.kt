package com.codewithkael.androidminichatwithwebrtc.cryptography.rsa

import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey

interface RSAService {
    suspend fun decryptText(encryptedText: String, publicKey: PublicKey): String?
    suspend fun decryptText(encryptedText: String, privateKey: PrivateKey): String?
    suspend fun encryptText(text: String, publicKey: PublicKey): String?
    suspend fun encryptText(text: String, privateKey: PrivateKey): String?
    fun convertKeyPairToBase64String(keyPair: KeyPair): Pair<String, String>
    fun convertBase64StringToKeyPair(publicKeyString: String, privateKeyString: String): KeyPair
    fun generateRSAKeyPair(keySize: Int): KeyPair
}