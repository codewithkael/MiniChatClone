package com.codewithkael.androidminichatwithwebrtc.cryptography.rsa


import android.util.Log
import com.codewithkael.androidminichatwithwebrtc.utils.MiniChatApplication.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher

class RSAServiceImpl : RSAService {

    private val asymmetricAlgorithm = "RSA/ECB/PKCS1Padding"

    private var rsaCipher: Cipher = Cipher.getInstance(asymmetricAlgorithm)
    private val rsaKeyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")

    override suspend fun encryptText(text: String, publicKey: PublicKey): String? =
        withContext(Dispatchers.Default) {
            Security.addProvider(BouncyCastleProvider())
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = try {
                rsaCipher.doFinal(text.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "encryptText: ${e.message}")
                return@withContext null
            }
            return@withContext Base64.getEncoder().encodeToString(encryptedBytes)
        }

    override suspend fun encryptText(text: String, privateKey: PrivateKey): String? =
        withContext(Dispatchers.Default) {
            Security.addProvider(BouncyCastleProvider())
            rsaCipher.init(Cipher.ENCRYPT_MODE, privateKey)
            val encryptedBytes = try {
                rsaCipher.doFinal(text.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
            return@withContext Base64.getEncoder().encodeToString(encryptedBytes)
        }

    override suspend fun decryptText(encryptedText: String, publicKey: PublicKey): String? =
        withContext(Dispatchers.Default) {
            Security.addProvider(BouncyCastleProvider())
            rsaCipher.init(Cipher.DECRYPT_MODE, publicKey)
            val decodedData = Base64.getDecoder().decode(encryptedText)
            try {
                val decryptedBytes = rsaCipher.doFinal(decodedData)
                String(decryptedBytes)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    override suspend fun decryptText(encryptedText: String, privateKey: PrivateKey): String? =
        withContext(Dispatchers.Default) {
            Security.addProvider(BouncyCastleProvider())
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decodedData = Base64.getDecoder().decode(encryptedText)
            try {
                val decryptedBytes = rsaCipher.doFinal(decodedData)
                String(decryptedBytes)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    override fun generateRSAKeyPair(keySize: Int): KeyPair {
        rsaKeyPairGenerator.initialize(keySize)
        return rsaKeyPairGenerator.generateKeyPair()
    }

    override fun convertKeyPairToBase64String(keyPair: KeyPair): Pair<String, String> {
        val publicKeyString = Base64.getEncoder().encodeToString(keyPair.public.encoded)
        val privateKeyString = Base64.getEncoder().encodeToString(keyPair.private.encoded)
        return Pair(publicKeyString, privateKeyString)
    }

    override fun convertBase64StringToKeyPair(
        publicKeyString: String,
        privateKeyString: String
    ): KeyPair {
        val publicKeyBytes = Base64.getDecoder().decode(publicKeyString)
        val privateKeyBytes = Base64.getDecoder().decode(privateKeyString)

        val keyFactory = KeyFactory.getInstance("RSA")

        val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)
        val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)

        val publicKey = keyFactory.generatePublic(publicKeySpec)
        val privateKey = keyFactory.generatePrivate(privateKeySpec)

        return KeyPair(publicKey, privateKey)
    }

    override fun base64ToPublicKey(base64: String): PublicKey {
        return try {
            val keyBytes = Base64.getDecoder().decode(base64)
            val keyFactory = KeyFactory.getInstance("RSA")
            val keySpec = X509EncodedKeySpec(keyBytes)
            keyFactory.generatePublic(keySpec)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to convert Base64 string to public key: ${e.message}", e)
        }
    }

    override fun base64ToPrivateKey(base64: String): PrivateKey {
        return try {
            val keyBytes = Base64.getDecoder().decode(base64)
            val keyFactory = KeyFactory.getInstance("RSA")
            val keySpec = PKCS8EncodedKeySpec(keyBytes)
            keyFactory.generatePrivate(keySpec)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to convert Base64 string to private key: ${e.message}", e)
        }
    }

    override fun publicKeyToBase64(publicKey: PublicKey): String {
        return try {
            Base64.getEncoder().encodeToString(publicKey.encoded)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to convert public key to Base64 string: ${e.message}", e)
        }
    }

    override fun privateKeyToBase64(privateKey: PrivateKey): String {
        return try {
            Base64.getEncoder().encodeToString(privateKey.encoded)
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to convert private key to Base64 string: ${e.message}", e)
        }
    }
}
