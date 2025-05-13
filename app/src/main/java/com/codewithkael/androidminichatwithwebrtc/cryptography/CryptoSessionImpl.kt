package com.codewithkael.androidminichatwithwebrtc.cryptography

import com.codewithkael.androidminichatwithwebrtc.cryptography.CryptoSession.HashFunctions.*
import com.codewithkael.androidminichatwithwebrtc.cryptography.aes.AESService
import com.codewithkael.androidminichatwithwebrtc.cryptography.aes.AESServiceImpl
import com.codewithkael.androidminichatwithwebrtc.cryptography.hash.HashService
import com.codewithkael.androidminichatwithwebrtc.cryptography.hash.MD5Hash
import com.codewithkael.androidminichatwithwebrtc.cryptography.hash.SHA256Hash
import com.codewithkael.androidminichatwithwebrtc.cryptography.hash.SHA512Hash
import com.codewithkael.androidminichatwithwebrtc.cryptography.rsa.RSAService
import com.codewithkael.androidminichatwithwebrtc.cryptography.rsa.RSAServiceImpl
import com.codewithkael.androidminichatwithwebrtc.cryptography.signiture.DigitalSignatureService
import com.codewithkael.androidminichatwithwebrtc.cryptography.signiture.RSADigitalSignature

class CryptoSessionImpl : CryptoSession {

    override fun getAESService(): AESService {
        return AESServiceImpl()
    }

    override fun getRSAService(): RSAService {
        return RSAServiceImpl()
    }

    override fun getHashService(function: CryptoSession.HashFunctions): HashService {
        return when(function){
            SHA256 -> SHA256Hash()
            SHA512 -> SHA512Hash()
            MD5 -> MD5Hash()
        }
    }

    override fun getRSADigitalSignatureService(rsaService: RSAService): DigitalSignatureService {
        return RSADigitalSignature(rsaService)
    }


}