package com.codewithkael.androidminichatwithwebrtc.cryptography

import com.codewithkael.androidminichatwithwebrtc.cryptography.aes.AESService
import com.codewithkael.androidminichatwithwebrtc.cryptography.hash.HashService
import com.codewithkael.androidminichatwithwebrtc.cryptography.rsa.RSAService
import com.codewithkael.androidminichatwithwebrtc.cryptography.signiture.DigitalSignatureService

interface CryptoSession {
    enum class HashFunctions {
        SHA256, SHA512, MD5
    }

    fun getAESService(): AESService
    fun getRSAService(): RSAService
    fun getHashService(function: HashFunctions): HashService
    fun getRSADigitalSignatureService(rsaService: RSAService): DigitalSignatureService
}