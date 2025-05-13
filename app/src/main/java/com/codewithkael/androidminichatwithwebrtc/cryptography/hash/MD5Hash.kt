package com.codewithkael.androidminichatwithwebrtc.cryptography.hash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

class MD5Hash : HashService {

    override fun hash(input: String): ByteArray {
        return hash(input.toByteArray())
    }

    override fun hash(input: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance("MD5")
        return digest.digest(input)
    }

    override suspend fun hash(file: File): ByteArray = withContext(Dispatchers.IO) {
        val digest = MessageDigest.getInstance("MD5")

        val fis = FileInputStream(file)
        val buffer = ByteArray(8192)
        var bytesRead = fis.read(buffer)
        while (bytesRead != -1) {
            digest.update(buffer, 0, bytesRead)
            bytesRead = fis.read(buffer)
        }
        fis.close()
        digest.digest()
    }

}
