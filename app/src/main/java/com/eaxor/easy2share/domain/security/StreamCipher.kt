package com.eaxor.easy2share.domain.security


import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.ByteBuffer
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


val secureRandom = SecureRandom()
const val nonceSize = 12


public suspend fun ByteArray.encryptChaCha20(key: ByteArray): ByteArray {

    val nonce = ByteArray(nonceSize)
    secureRandom.nextBytes(nonce)

    val iv = IvParameterSpec(nonce)
    val secretKey = SecretKeySpec(key, "ChaCha20-Poly1305")

    // A fresh Cipher per call keeps encryption thread-safe.
    val chachaCipher = Cipher.getInstance("ChaCha20-Poly1305")
    chachaCipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)

    val encryptedMessage = chachaCipher.doFinal(this)
    val output: ByteArray = ByteBuffer.allocate(encryptedMessage.size + nonce.size)
        .put(encryptedMessage)
        .put(nonce)
        .array()

    return output
}

public suspend fun ByteArray.decryptChaCha20(key: ByteArray): ByteArray {
    val bb = ByteBuffer.wrap(this)

    // split cText to get the appended nonce
    val encryptedText = ByteArray(this.size - nonceSize)
    val nonce = ByteArray(nonceSize)
    bb.get(encryptedText)
    bb.get(nonce)

    val iv = IvParameterSpec(nonce)
    val secretKey = SecretKeySpec(key, "ChaCha20-Poly1305")

    // A fresh Cipher per call keeps decryption thread-safe.
    val chachaCipher = Cipher.getInstance("ChaCha20-Poly1305")
    chachaCipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
    return chachaCipher.doFinal(encryptedText)
}