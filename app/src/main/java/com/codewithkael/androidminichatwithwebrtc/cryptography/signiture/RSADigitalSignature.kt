package com.codewithkael.androidminichatwithwebrtc.cryptography.signiture

import com.codewithkael.androidminichatwithwebrtc.cryptography.rsa.RSAService
import java.security.PrivateKey
import java.security.PublicKey

class RSADigitalSignature(
    private val rsaService: RSAService
) : DigitalSignatureService {


    override suspend fun sign(message: String, privateKey: PrivateKey): String? {
        return rsaService.encryptText(message, privateKey)
    }

    override suspend fun verify(
        signature: String,
        message: String,
        publicKey: PublicKey
    ): Boolean {
        val decryptedSignature = rsaService.decryptText(signature, publicKey)
        return decryptedSignature?.contentEquals(message) ?: false
    }

}