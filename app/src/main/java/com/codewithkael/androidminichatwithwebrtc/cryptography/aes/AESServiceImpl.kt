package com.codewithkael.androidminichatwithwebrtc.cryptography.aes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.Security
import java.util.Arrays
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class AESServiceImpl : AESService {

    private val symmetricAlgorithm = "AES/CBC/PKCS5PADDING"
    private val ivSize = 16 //depending on our algorithm

    private var cipher: Cipher = Cipher.getInstance(symmetricAlgorithm)
    private val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")

    override fun generateKey(keyLength: Int): SecretKey {
        keyGenerator.init(keyLength)
        return keyGenerator.generateKey()
    }

    override fun convertKeyToString(secretKey: SecretKey): String {
        return Base64.getEncoder().encodeToString(secretKey.encoded)
    }

    override fun convertStringToKey(keyString: String): SecretKey {
        val decodedBytes = Base64.getDecoder().decode(keyString)
        return SecretKeySpec(decodedBytes, symmetricAlgorithm)
    }

    override suspend fun encryptText(text: String, key: SecretKey): String? =
        withContext(Dispatchers.Default) {
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val encryptedBytes = try {
                cipher.doFinal(text.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
            val iv = cipher.iv
            val combinedIvAndEncryptedData = iv + encryptedBytes
            return@withContext Base64.getEncoder().encodeToString(combinedIvAndEncryptedData)
        }

    override suspend fun decryptText(encryptedText: String, key: SecretKey): String? =
        withContext(Dispatchers.Default) {
            val decodedData = Base64.getDecoder().decode(encryptedText)
            val iv = Arrays.copyOfRange(decodedData, 0, ivSize)
            val encryptedBytes = Arrays.copyOfRange(decodedData, ivSize, decodedData.size)

            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            try {
                val decryptedBytes = cipher.doFinal(encryptedBytes)
                String(decryptedBytes)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    override suspend fun encryptFile(inputFile:File,outputFile:File,key: SecretKey): File? =
        withContext(Dispatchers.IO) {
            return@withContext try {
                Security.addProvider(BouncyCastleProvider())
                cipher.init(Cipher.ENCRYPT_MODE, key)
                val iv = cipher.iv

                val inputStream = FileInputStream(inputFile)

                val outputStream = FileOutputStream(outputFile)
                outputStream.write(iv)

                val cipherOutputStream = CipherOutputStream(outputStream, cipher)
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead)
                }
                cipherOutputStream.close()
                inputStream.close()

                outputFile
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun decryptFile(encryptedFile: File, outputFile: File, key: SecretKey): File? =
        withContext(Dispatchers.IO){
        return@withContext try {
            Security.addProvider(BouncyCastleProvider())

            val encryptedDataInputStream = FileInputStream(encryptedFile)
            val iv = ByteArray(ivSize)
            encryptedDataInputStream.read(iv, 0, ivSize)

            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))

            val outputStream = FileOutputStream(outputFile)

            val cipherInputStream = CipherInputStream(encryptedDataInputStream, cipher)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (cipherInputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            cipherInputStream.close()

            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
